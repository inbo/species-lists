package au.org.ala.listsapi.config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import au.org.ala.names.ws.api.NameSearch;
import au.org.ala.names.ws.api.NameUsageMatch;

@Configuration
@ImportRuntimeHints(NameMatchClientHints.class)
public class NameMatchClientHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // NameSearch and NameUsageMatch use Lombok @Builder with reflection-based
        // with* method lookup (WITH_METHODS) and field introspection (HINT_FIELDS,
        // RANK_FIELDS) at static-init time. @RegisterReflectionForBinding only
        // covers a subset of member categories, so we register all of them here.
        hints.reflection().registerType(NameSearch.class, MemberCategory.values());
        hints.reflection().registerType(NameUsageMatch.class, MemberCategory.values());

        // Lombok-generated builder classes: Jackson looks up the build() method
        // via reflection during deserialization.
        hints.reflection().registerType(NameSearch.NameSearchBuilder.class, MemberCategory.values());
        hints.reflection().registerType(NameUsageMatch.NameUsageMatchBuilder.class, MemberCategory.values());

        try {
            Class<?> retrofitService =
                    Class.forName("au.org.ala.names.ws.client.ALANameUsageMatchRetrofitService");
            hints.reflection().registerType(
                    retrofitService, MemberCategory.values());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "Cannot register ALANameUsageMatchRetrofitService for native-image reflection", e);
        }
    }
}
