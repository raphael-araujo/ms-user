package com.raphael.msuser.web.client;

import com.raphael.msuser.web.dto.CepClientDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "msaddress", url = "${msaddress.url}", path = "v1/address")
public interface AddressClient {

    @PostMapping
    Void saveAddressWithUser(@RequestParam("cep") String cep,
                             @RequestParam("email") String email);

    @GetMapping("/{cep}")
    CepClientDto findByCep(@PathVariable String cep);
}
