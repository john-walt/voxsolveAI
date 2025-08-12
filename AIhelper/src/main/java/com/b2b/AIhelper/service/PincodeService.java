package com.b2b.AIhelper.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.b2b.AIhelper.entity.Address;
import com.b2b.AIhelper.repository.AddressRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class PincodeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AddressRepository addressRepository;

    public Address fetchFirstAddressFromPincode(String pincode) {
        String url = "https://api.postalpincode.in/pincode/" + pincode;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            JsonNode postOffices = root.get(0).get("PostOffice");

            if (postOffices != null && postOffices.isArray() && postOffices.size() > 0) {
                JsonNode node = postOffices.get(0); // Take only the first

                Address address = new Address();
                address.setName(node.get("Name").asText());
                address.setBlock(node.get("Block").asText());
                address.setRegion(node.get("Region").asText());
                address.setDistrict(node.get("District").asText());
                address.setState(node.get("State").asText());
                address.setPin(node.get("Pincode").asText());

                return address;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // If nothing is fetched
    }

}


