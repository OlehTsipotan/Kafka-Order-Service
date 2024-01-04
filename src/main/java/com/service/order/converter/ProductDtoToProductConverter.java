package com.service.order.converter;

import com.service.order.dto.ProductDto;
import com.service.order.model.Product;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ProductDtoToProductConverter implements Converter<ProductDto, Product> {

    private final ModelMapper modelMapper;


    public ProductDtoToProductConverter() {
        this.modelMapper = new ModelMapper();
    }

    @Override
    public Product convert(@NonNull ProductDto source) {
        return modelMapper.map(source, Product.class);
    }
}
