package com.exchanger.exchange_api.controller;

import com.exchanger.exchange_api.dto.ErrorResponseDTO;
import com.exchanger.exchange_api.dto.request.ConversionRequestDTO;
import com.exchanger.exchange_api.dto.response.ConversionListResponseDTO;
import com.exchanger.exchange_api.dto.response.ConversionResponseDTO;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.exchanger.exchange_api.service.ConversionService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PastOrPresent;
import java.util.Date;

@RestController
@RequestMapping("conversion")
@Validated
@Api(tags = {"Conversion API"})
public class ConversionController {
    private final ConversionService conversionService;

    @Autowired
    public ConversionController(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ApiOperation("Convert a given amount between two currencies")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success",
                    response = ConversionResponseDTO.class),
            @ApiResponse(code = 400, message = "Validation/Conversion error",
                    response = ErrorResponseDTO.class),
    })
    public ResponseEntity<ConversionResponseDTO> postConversion(
            @RequestBody @Validated ConversionRequestDTO body) throws HttpResponseException {
        return new ResponseEntity<>(conversionService.convert(body.getAmount(), body.getSource(), body.getTarget()), HttpStatus.OK);
    }

    @GetMapping(produces = "application/json")
    @ApiOperation("Get the conversions by an id or in a specific date")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success",
                    response = ConversionListResponseDTO.class),
            @ApiResponse(code = 400, message = "Validation error/Missing data",
                    response = ErrorResponseDTO.class),
    })
    public ResponseEntity<ConversionListResponseDTO> getConversions(
            @RequestParam(required = false) @ApiParam(example = "UUID") String id,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy")
            @PastOrPresent @ApiParam(format = "dd.MM.yyyy", example = "30.12.2012") Date date,
            @RequestParam(required = false, defaultValue = "0") @Min(0) int page,
            @RequestParam(required = false, defaultValue = "10") @Min(1) @Max(100) int pageAmount) throws HttpResponseException {
        return new ResponseEntity<>(conversionService.listTransactions(id, date, page, pageAmount), HttpStatus.OK);
    }
}
