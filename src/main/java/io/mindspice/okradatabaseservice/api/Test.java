package io.mindspice.okradatabaseservice.api;

import io.mindspice.mindlib.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;



@CrossOrigin
@RestController
public class Test {

    @PostMapping("/health")
    public ResponseEntity<String> health(@RequestBody String jsonReq) throws IOException {
        return new ResponseEntity<>(JsonUtils.readTree(jsonReq).get("ping").asText(), HttpStatus.OK);
    }

}
