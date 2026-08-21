# Modelos serializados pelo kotlinx.serialization sao referenciados por reflexao
# apenas atraves dos serializadores gerados; mantemos os serializadores.
-keepclassmembers class br.com.w3ti.indicio.** {
    *** Companion;
}
-keepclasseswithmembers class br.com.w3ti.indicio.** {
    kotlinx.serialization.KSerializer serializer(...);
}
