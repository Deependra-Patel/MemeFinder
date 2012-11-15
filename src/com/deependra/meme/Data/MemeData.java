package com.deependra.meme.Data;

import java.util.Map;

import lombok.Value;

@Value
public class MemeData {
    Map<Long, Meme> _default;
}
