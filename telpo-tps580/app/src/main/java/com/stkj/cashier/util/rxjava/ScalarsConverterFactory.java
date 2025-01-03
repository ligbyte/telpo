package com.stkj.cashier.util.rxjava;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import com.stkj.cashier.util.rxjava.ScalarResponseBodyConverters.BooleanResponseBodyConverter;
import com.stkj.cashier.util.rxjava.ScalarResponseBodyConverters.ByteResponseBodyConverter;
import com.stkj.cashier.util.rxjava.ScalarResponseBodyConverters.CharacterResponseBodyConverter;
import com.stkj.cashier.util.rxjava.ScalarResponseBodyConverters.DoubleResponseBodyConverter;
import com.stkj.cashier.util.rxjava.ScalarResponseBodyConverters.FloatResponseBodyConverter;
import com.stkj.cashier.util.rxjava.ScalarResponseBodyConverters.IntegerResponseBodyConverter;
import com.stkj.cashier.util.rxjava.ScalarResponseBodyConverters.LongResponseBodyConverter;
import com.stkj.cashier.util.rxjava.ScalarResponseBodyConverters.ShortResponseBodyConverter;
import com.stkj.cashier.util.rxjava.ScalarResponseBodyConverters.StringResponseBodyConverter;

/**
 * A {@linkplain Converter.Factory converter} for strings and both primitives and their boxed types
 * to {@code text/plain} bodies.
 */
public final class ScalarsConverterFactory extends Converter.Factory {
    public static ScalarsConverterFactory create() {
        return new ScalarsConverterFactory();
    }

    private ScalarsConverterFactory() {}

    @Override
    public @Nullable Converter<?, RequestBody> requestBodyConverter(
            Type type,
            Annotation[] parameterAnnotations,
            Annotation[] methodAnnotations,
            Retrofit retrofit) {
        if (type == String.class
                || type == boolean.class
                || type == Boolean.class
                || type == byte.class
                || type == Byte.class
                || type == char.class
                || type == Character.class
                || type == double.class
                || type == Double.class
                || type == float.class
                || type == Float.class
                || type == int.class
                || type == Integer.class
                || type == long.class
                || type == Long.class
                || type == short.class
                || type == Short.class) {
            return ScalarRequestBodyConverter.INSTANCE;
        }
        return null;
    }

    @Override
    public @Nullable Converter<ResponseBody, ?> responseBodyConverter(
            Type type, Annotation[] annotations, Retrofit retrofit) {
        if (type == String.class) {
            return StringResponseBodyConverter.INSTANCE;
        }
        if (type == Boolean.class || type == boolean.class) {
            return BooleanResponseBodyConverter.INSTANCE;
        }
        if (type == Byte.class || type == byte.class) {
            return ByteResponseBodyConverter.INSTANCE;
        }
        if (type == Character.class || type == char.class) {
            return CharacterResponseBodyConverter.INSTANCE;
        }
        if (type == Double.class || type == double.class) {
            return DoubleResponseBodyConverter.INSTANCE;
        }
        if (type == Float.class || type == float.class) {
            return FloatResponseBodyConverter.INSTANCE;
        }
        if (type == Integer.class || type == int.class) {
            return IntegerResponseBodyConverter.INSTANCE;
        }
        if (type == Long.class || type == long.class) {
            return LongResponseBodyConverter.INSTANCE;
        }
        if (type == Short.class || type == short.class) {
            return ShortResponseBodyConverter.INSTANCE;
        }
        return null;
    }
}
