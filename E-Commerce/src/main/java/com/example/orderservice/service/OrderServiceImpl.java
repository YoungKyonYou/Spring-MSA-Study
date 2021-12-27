package com.example.orderservice.service;



import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Data
@Slf4j
@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    @Override
    public OrderDto createOrder(OrderDto orderDto) {
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty()*orderDto.getUnitPrice());


        //DB에 저장하기 위해서 Entity가 필요하니까 DTO->Entity로 바꾸는 작업을 하자.
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderEntity orderEntity = mapper.map(orderDto, OrderEntity.class);


        orderRepository.save(orderEntity);

        OrderDto returnValue = mapper.map(orderDto, OrderDto.class);

        return returnValue;
    }

    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        OrderEntity orderEntity=orderRepository.findByProductId(orderId);
        OrderDto orderDto=new ModelMapper().map(orderEntity,OrderDto.class);
        return orderDto;
    }

    @Override
    public Iterable<OrderEntity> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
