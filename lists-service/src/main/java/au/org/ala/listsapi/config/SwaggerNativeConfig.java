package au.org.ala.listsapi.config;

import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.providers.SpringWebProvider;
import org.springdoc.webmvc.ui.SwaggerWelcomeWebMvc;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Native-image workaround for springdoc-openapi.
 *
 * <p>springdoc's {@code org.springdoc.webmvc.ui.SwaggerConfig#swaggerWelcome(...)} declares its
 * {@link SpringWebProvider} parameter as {@link org.springframework.context.annotation.Lazy @Lazy}.
 * Because {@code SpringWebProvider} is an abstract class (not an interface), Spring builds the lazy
 * resolution proxy as a <em>CGLIB subclass</em>. CGLIB cannot generate classes at runtime in a
 * GraalVM native image, so the {@code swaggerWelcome} bean fails to instantiate with:
 *
 * <pre>
 * org.springframework.aop.framework.AopConfigException:
 *   Could not generate CGLIB subclass of class org.springdoc.core.providers.SpringWebProvider
 * </pre>
 *
 * <p>which in turn cascades into the {@code WebSecurityConfiguration} bean creation failure seen at
 * startup.
 *
 * <p>This configuration provides an equivalent {@code swaggerWelcome} bean that injects
 * {@link SpringWebProvider} <em>eagerly</em> (no proxy, no runtime CGLIB). springdoc's own
 * definition is annotated {@code @ConditionalOnMissingBean}, so it backs off in favour of this one.
 * The conditions mirror springdoc's so behaviour is unchanged when the Swagger UI is disabled.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "springdoc.swagger-ui.enabled", matchIfMissing = true)
public class SwaggerNativeConfig {

    /**
     * Replacement for springdoc's {@code swaggerWelcome} bean that avoids the {@code @Lazy} CGLIB
     * proxy on {@link SpringWebProvider}, which is incompatible with GraalVM native images.
     */
    @Bean
    @ConditionalOnProperty(
            name = "springdoc.use-management-port",
            havingValue = "false",
            matchIfMissing = true)
    public SwaggerWelcomeWebMvc swaggerWelcome(
            SwaggerUiConfigProperties swaggerUiConfig,
            SpringDocConfigProperties springDocConfigProperties,
            SpringWebProvider springWebProvider) {
        return new SwaggerWelcomeWebMvc(
                swaggerUiConfig, springDocConfigProperties, springWebProvider);
    }
}
