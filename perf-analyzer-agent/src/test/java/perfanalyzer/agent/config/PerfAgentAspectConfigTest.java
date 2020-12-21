package perfanalyzer.agent.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PerfAgentAspectConfigTest {

	@Test
	public void testPatterns() throws Exception {
		PerfAgentAspectConfig cfg = new PerfAgentAspectConfig();
		cfg.setIncludeClasses("com.example.*;com.example2.*");
		cfg.setExcludeClasses("com.example.*Model*");
		assertTrue(cfg.matchClass("com.example.FooService"));
		assertTrue(cfg.matchClass("com.example2.FooService"));
		assertFalse(cfg.matchClass("com.example.FooModel"));
		assertFalse(cfg.matchClass("com.example.FooModelBar"));
		assertFalse(cfg.matchClass("com.example.Model01"));
		assertFalse(cfg.matchClass("com.example.service.FooService"));
		
		cfg = new PerfAgentAspectConfig();
		cfg.setIncludeClasses("com.example.**;com.example2.**");
		cfg.setExcludeClasses("com.example.**Model*");
		assertTrue(cfg.matchClass("com.example.FooService"));
		assertTrue(cfg.matchClass("com.example2.FooService"));
		assertFalse(cfg.matchClass("com.example.FooModel"));
		assertFalse(cfg.matchClass("com.example.FooModelBar"));
		assertFalse(cfg.matchClass("com.example.Model01"));
		assertTrue(cfg.matchClass("com.example.service.FooService"));
		assertTrue(cfg.matchClass("com.example2.service.FooService"));
		assertTrue(cfg.matchClass("com.example.model.Foo"));
		assertFalse(cfg.matchClass("com.example.model.FooModel"));
		cfg = new PerfAgentAspectConfig();
		
		cfg.setIncludeMethods("*");
		cfg.setExcludeMethods("get*;set*");
		assertTrue(cfg.matchMethod("update"));
		assertTrue(cfg.matchMethod("doGet"));
		assertFalse(cfg.matchMethod("getName"));
		assertFalse(cfg.matchMethod("getname"));
		assertFalse(cfg.matchMethod("setName"));
	}

}
