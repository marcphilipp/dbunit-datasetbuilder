# DBUnit - Dynamically Creating Data Sets Using Builders

[![Build Status](https://travis-ci.org/marcphilipp/dbunit-datasetbuilder.png)](https://travis-ci.org/marcphilipp/dbunit-datasetbuilder)

## Motivation

DBUnit is a very useful library for writing tests that use relational databases. For example, it allows to cleanly insert required data before a test.

Usually, the data set to be loaded into the database is read from an XML file, such as this `dataset.xml`:

    <dataset>
      <PERSON NAME="Bob" AGE="18"/>
      <PERSON NAME="Alice" AGE="23"/>
      <PERSON NAME="Charlie" LAST_NAME="Brown"/>
    </dataset>

DBUnit can then load this file like this:

    IDataSet dataSet = new FlatXmlDataSetBuilder().build(new FileInputStream("dataset.xml"));

Next, you can use DBUnit to store the `IDataSet` in the database:

    DatabaseTester databaseTester = new JdbcDatabaseTester(
            "org.hsqldb.jdbcDriver", "jdbc:hsqldb:sample", "sa", "");
    databaseTester.setDataSet( dataSet );
    databaseTester.onSetup();

The default `onSetup()` operation is `CLEAN_INSERT` will delete all data in all the tables contained in the data set and then insert the specified rows. How this is done depends on the kind of database used, of course. DBUnit will figure out how to do it on its own. You will not have to write your own SQL queries anymore.

While this is incredibly useful, understanding the resulting tests is often hard because you have to switch back and forth between multiple files, i.e. the actual test code and the XML data set. So, the idea was born: Wouldn't it be possible to leverage the power of DBUnit but create the data set right in your test code?

## Solution

After researching and looking through the DBUnit code -- especially `FlatXmlDataSetBuilder` and the classes it uses -- for a while, I figured it possible but there was no nice, readable way to do it, yet. Therefore, I came up with a class called `DataSetBuilder` which is basically a wrapper around a `CachedDataSet` using a `BufferedConsumer`. Let's take a look at an example:

    DataSetBuilder builder = new DataSetBuilder();

    // Using strings as column names, not type-safe
    builder.newRow("PERSON").with("NAME", "Bob").with("AGE", 18).add();

    // Using ColumnSpecs to identify columns, type-safe!
    ColumnSpec<String> name = ColumnSpec.newColumn("NAME")
    ColumnSpec<Integer> age = ColumnSpec.newColumn("AGE");
    builder.newRow("PERSON").with(name, "Alice").with(age, 23).add();

    // New columns are added on the fly
    builder.newRow("PERSON").with(name, "Charlie").with("LAST_NAME", "Brown").add();

    IDataSet dataSet = builder.build();

The code listed above creates three records in the `PERSON` table. It showcases two different ways of specifying columns, one using plain Strings and one using `ColumnSpec` instances, respectively.

You might now print it out to the console or a file, i.e.

    new FlatXmlWriter(new PrintWriter(System.out)).write(dataSet);

will print

    <dataset>
      <PERSON NAME="Bob" AGE="18"/>
      <PERSON NAME="Alice" AGE="23"/>
      <PERSON NAME="Charlie" LAST_NAME="Brown"/>
    </dataset>

I think I found a way to create a data set directly from Java code in a readable way. In addition, creating the data sets programmatically gives to tools like refactoring, search for references, and so on for free.

## Solution - Step 2
The idea to create a data set directly from Java code is great and give new possibilities, for example use a sequence for ids.
However the code is still boiler plated. SO the second natural step is to create table specific row builder.
With them the code can be more compact.

     DataSetBuilder builder = new DataSetBuilder();
     newPERSON().NAME("Bob").BIRTHPLACE("NEW YORK").addTo(builder);
     newPERSON().NAME("Alice").BIRTHPLACE("London").addTo(builder);


with this the code is type-safe and compact. And to make it really comfortable
there should be a generator for this builders - `CustomRowBuilderGenerator.java`.
You can use it very easily:

    CustomRowBuilderGenerator rowBuilder = new CustomRowBuilderGenerator(
        new File("src/test/java"), "net.sf.sze.dbunit.rowbuilder", "UTF-8");
    rowBuilder.addTypeMapping(BigInteger.class, Long.class);
    rowBuilder.addTypeMapping(BigDecimal.class, Double.class);
    rowBuilder.generate(getConnection().createDataSet());

With this preparation you can dump dataset from the database with

    BuilderDataSetWriter writer =  new BuilderDataSetWriter(
            new File("src/test/java"), "net.sf.sze.dbunit.dataset",
            "ResultDS", "UTF-8", "net.sf.sze.dbunit.rowbuilder", true, importStatements);
    writer.addTypeMapping(BigInteger.class, Long.class);
    writer.addTypeMapping(BigDecimal.class, Double.class);
    writer.write(getConnection().createDataSet());

You can combine this with [Validators dbunit-validation](https://github.com/opensource21/dbunit-validation)
to make your test more robust.

A further improvement is the DataSetRowChanger. This allows you to define
which rows of of a given dataset should change. To use this you must declare
identifier columns which should be a unique key.
