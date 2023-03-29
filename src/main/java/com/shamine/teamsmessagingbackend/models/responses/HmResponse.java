package com.shamine.teamsmessagingbackend.models.responses;

import com.shamine.teamsmessagingbackend.dto.ResponseDto;

import java.util.HashMap;

public class HmResponse {
    private final HashMap<String, Object> hashMap;

    public HmResponse()
    {
        hashMap = new HashMap<>();
    }

    public void setResponse(ResponseDto response)
    {
        hashMap.put("response", response);
    }

    public HashMap<String, Object> getHashMap()
    {
        return hashMap;
    }
}
