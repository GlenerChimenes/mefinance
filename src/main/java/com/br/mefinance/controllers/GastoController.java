package com.br.mefinance.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.Year;

@RestController
@RequestMapping("/gastos")
public class GastoController {

    BigDecimal rendaBruta = new BigDecimal("12500.00");


}
