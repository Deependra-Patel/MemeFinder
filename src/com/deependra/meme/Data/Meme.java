package com.deependra.meme.Data;

import lombok.Value;

@Value
public class Meme {
    String title;
    Thumbnail thumbnail;
    Long created_utc;
    String author;
    String id;
    Long ups;
    Long downs;
    String media;
}
