# Modelos serializados pelo kotlinx.serialization sao referenciados por reflexao
# apenas atraves dos serializadores gerados; mantemos os serializadores.
-keepclassmembers class br.com.avoren.indicio.** {
    *** Companion;
}
-keepclasseswithmembers class br.com.avoren.indicio.** {
    kotlinx.serialization.KSerializer serializer(...);
}
