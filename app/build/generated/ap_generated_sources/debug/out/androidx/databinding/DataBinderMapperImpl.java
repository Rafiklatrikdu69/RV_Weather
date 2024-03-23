package androidx.databinding;

public class DataBinderMapperImpl extends MergedDataBinderMapper {
  DataBinderMapperImpl() {
    addMapper(new com.bouchenna.rv_weather.DataBinderMapperImpl());
  }
}
