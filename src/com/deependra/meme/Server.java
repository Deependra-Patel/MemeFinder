package com.deependra.meme;

import java.io.IOException;

import org.rapidoid.setup.On;

public class Server {

    public static void main(String[] args) throws IOException {
        Index index = new Index();
        On.get("/search").json((String input) -> index.searchIndex(input));
    }
}
