package com.yohan.reflectjson;

import com.yohan.reflectjson.annotations.JsonIgnore;
import com.yohan.reflectjson.annotations.JsonProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class JsonMapperTest {

    private final JsonMapper jsonMapper = new JsonMapper();

    @Test
    @DisplayName("Should return {} for empty objects")
    void shouldReturnEmptyObject(){
        //given
        var input = new Object();
        //when
        var actual = jsonMapper.toJson(input);
        //then
        var expected = "{}";
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Should correctly map string fields in objects")
    void shouldCorrectlyMapStringFields() {
        //given
        record Person(String firstName, String lastName){}
        var input = new Person("John", "Doe");
        //when
        var actual = jsonMapper.toJson(input);
        //then
        //{"firstName":"John","lastName":"Doe"}
        var expected = "{\"firstName\":\"John\",\"lastName\":\"Doe\"}";
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Should correctly map Integer fields")
    void shouldCorrectlyMapIntegerFields() {
        //given
        record BlogPost(String title, int likes){}
        var input = new BlogPost("JSON Mapper Test", 42);
        //when
        var actual = jsonMapper.toJson(input);
        //then
        var expected = "{\"title\":\"JSON Mapper Test\",\"likes\":42}";
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Should correctly map primitive fileds")
    void shouldCorrectlyMapPrimitiveFields() {
        //given
        record Stock(String title , float price){}
        var input = new Stock("Bitcoin", 42.345f);
        //when
        var actual = jsonMapper.toJson(input);
        //then
        var expected = "{\"title\":\"Bitcoin\",\"price\":42.345}";
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Should correctly map boolean fileds")
    void shouldCorrectlyMapBooleanFields() {
        //given
        record Stock(String title , boolean available){}
        var input = new Stock("Bitcoin", false);
        //when
        var actual = jsonMapper.toJson(input);
        //then
        var expected = "{\"title\":\"Bitcoin\",\"available\":false}";
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Should ignore fileds with @JsonIgnore fileds")
    void shouldIgnoreJsonIgnoreFields() {
        //given
        record Book(String title ,@JsonIgnore String doi){}
        var input = new Book("Bitcoin", "secret");
        //when
        var actual = jsonMapper.toJson(input);
        //then
        var expected = "{\"title\":\"Bitcoin\"}";
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Should rename fields with @JsonProperty")
    void shouldRenameFieldsWithJsonProperty() {
        //given
        record Book(String title ,@JsonProperty("secret") int viewsecret){}
        var input = new Book("Bitcoin", 23);
        //when
        var actual = jsonMapper.toJson(input);
        //then
        var expected = "{\"title\":\"Bitcoin\",\"secret\":23}";
        Assertions.assertEquals(expected,actual);
    }

    @Test
    @DisplayName("Should ignore duplicate definitions ")
    void shouldIgnoreDuplicates() {
        //given
        class Book{

            private String title;

            @JsonProperty("title")
            public String getTitle() {
                return title;
            }
        }
        var input = new Book();
        input.title = "JAVA";
        //when
        var actual = jsonMapper.toJson(input);
        //then
        var expected = "{\"title\":\"JAVA\"}";
        Assertions.assertEquals(expected,actual);
    }

}
