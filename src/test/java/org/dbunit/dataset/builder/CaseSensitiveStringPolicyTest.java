package org.dbunit.dataset.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CaseSensitiveStringPolicyTest {

	private CaseSensitiveStringPolicy policy = new CaseSensitiveStringPolicy();
	
	@Test
	public void keyIsSameAsInputString() {
		assertEquals("kEy1", policy.toKey("kEy1"));
	}
	
	@Test
	public void caseIsConsideredWhenComparingTwoStrings() throws Exception {
		assertFalse(policy.areEqual("KEY1", "kEy1"));
		assertTrue(policy.areEqual("KEY1", "KEY1"));
	}
}
