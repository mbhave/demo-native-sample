package org.springframework.boot;

import com.example.demonativesample.DemoNativeSampleApplication;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;


class AotSpringApplicationConfigurer {

	public static void main(String[] args) {
		SpringApplicationHooks.withHook(new SpringApplicationHooks.Hook() {
			@Override
			public void preRun(SpringApplication application) {
				application.setApplicationContextFactory((webApplicationType) -> new GenericApplicationContext());
				application.setMainApplicationClass(DemoNativeSampleApplication.class);
				application.setWebApplicationType(WebApplicationType.NONE);
				addApplicationContextInitializer(application);
			}
		}, () -> callApplicationMainMethod(args));
	}

	private static void addApplicationContextInitializer(SpringApplication application) {
		try {
			Class<?> aClass = Class.forName(DemoNativeSampleApplication.class.getName() + "__ApplicationContextInitializer",
					true, application.getClassLoader());
			ApplicationContextInitializer<?> initializer = (ApplicationContextInitializer<?>) aClass
					.getDeclaredConstructor().newInstance();
			application.addInitializers(initializer);
		}
		catch (Exception ex) {
			throw new IllegalArgumentException("Failed to configure AOT context", ex);
		}
	}

	private static void callApplicationMainMethod(String[] args) {
		try {
			DemoNativeSampleApplication.main(args);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
