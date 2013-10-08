package org.dbunit.dataset.builder;

import static org.junit.Assert.*;

import org.junit.Test;

public class CaseInsensitiveStringPolicyTest {

	private CaseInsensitiveStringPolicy policy = new CaseInsensitiveStringPolicy();
	
	@Test
	public void keyIsStringInUpperCase() {
		assertEquals("KEY1", policy.toKey("kEy1"));
	}
	
	@Test
	public void caseIsIgnoredWhenComparingTwoStrings() throws Exception {
		assertTrue(policy.areEqual("KEY1", "kEy1"));
		assertFalse(policy.areEqual("KEY1", "KEY2"));
	}
}
