package it.sella.f24.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.sella.f24.bean.auth.AuthServiceInput;
import it.sella.f24.service.Authservice;

@RestController
public class AuthController {

	@Autowired
	private Authservice authservice;

	@RequestMapping(value = "/api/authcheck", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> authCheck(@RequestBody AuthServiceInput authServiceInput) {

		
			return this.authservice.authorise(authServiceInput);
		
		

	}

}
