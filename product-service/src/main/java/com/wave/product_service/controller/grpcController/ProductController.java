package com.wave.product_service.controller.grpcController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wave.product.Product.AddProductResponse;
import com.wave.product.Product.ProductDto;
import com.wave.product.ProductServiceGrpc.ProductServiceImplBase;
import com.wave.product_service.service.ProductService;
import com.wave.utility.StreamObserverUtility;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class ProductController extends ProductServiceImplBase
{
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    private AddProductResponse addProduct(ProductDto request)
    {
        log.info("Received request: {}", request);
        try
        {
            productService.save(request);

            return AddProductResponse
                .newBuilder()
                .setStatusCode(200)
                .setMessage(request.getId() + ": OK")
                .build();
        }
        catch(Exception e)
        {
            return AddProductResponse
                .newBuilder()
                .setStatusCode(500)
                .setMessage(e.getMessage())
                .build();
        }
    }

    @Override
    public StreamObserver<ProductDto> addProduct(StreamObserver<AddProductResponse> responseObserver)
    {
        return StreamObserverUtility.proxyStream(responseObserver, this::addProduct);
    }
}
