package org.acme.lambda;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.acme.lambda.model.Course;
//import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

@Path("/api/media")
public class FileMediaResource {

    Logger logger = LoggerFactory.getLogger(FileMediaResource.class);

    ObjectMapper mapper = new ObjectMapper();
//    public static class FormData {
//        @FormParam("file")
//        public InputStream file;
//    }


    private static final Region REGION = Region.US_EAST_1;

    private final S3Presigner presigner;

    public FileMediaResource() {
        this.presigner = S3Presigner.builder()
                .region(REGION)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
    }


    @Path("/upload")
    @PUT
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response uploadFile(@QueryParam("bucketName") String bucketName,
                               @QueryParam("fileName") String fileName,
                               @HeaderParam("Content-Length") long contentLength,
                               String fileInputStream
//                               InputStream fileInputStream
//                               @FormDataParam("file") InputStream fileInputStream,
//                               @MultipartForm FormData formData
    ) {

        if (bucketName == null || fileName == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Bucket name and file name must be provided").build();
        }


        try {
            logger.info("Received upload data = " + mapper.writeValueAsString(fileInputStream));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        S3Client s3 = S3Client.create();
        try {

            byte[] decodedBytes = Base64.getDecoder().decode(fileInputStream); //fileInputStream.readAllBytes());
            logger.info("Decoded upload data = " + decodedBytes);
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3.putObject(request, RequestBody.fromBytes(decodedBytes));
//            s3.putObject(request, RequestBody.fromInputStream(fileInputStream, contentLength));
//            s3.putObject(request, RequestBody.fromInputStream(fileInputStream, -1));
//            s3.putObject(request, RequestBody.fromInputStream(formData.file, -1));
            return Response.ok("File uploaded successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to upload file: " + e.getMessage()).build();
        } finally {
            s3.close();
        }
    }

    @Path("/download")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response downloadFile(@QueryParam("bucketName") String bucketName,
                                 @QueryParam("fileName") String fileName) {

        if (bucketName == null || fileName == null) {
            logger.info("Bucket name and file name must be provided");
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Bucket name and file name must be provided").build();
        }

        S3Client s3 = S3Client.create();
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            logger.info("Before s3 get object");
            InputStream inputStream = s3.getObject(request, ResponseTransformer.toInputStream());

//            logger.info("Received download data = " + mapper.writeValueAsString(inputStream));

            return Response.ok((StreamingOutput) outputStream -> {
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
//                        logger.info("Decoded download data = " + buffer);
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    inputStream.close();
                } catch (IOException e) {
                    logger.info("Failed to read file from S3: " + e.getMessage());
                    throw new RuntimeException("Failed to read file from S3: " + e.getMessage());
                }
            }).build();
        } catch (Exception e) {
            logger.info("Failed to download file: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to download file: " + e.getMessage()).build();
        } finally {
            s3.close();
        }
    }

    @Path("/signed-url")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getSignedUrl(@QueryParam("bucketName") String bucketName,
                                 @QueryParam("fileName") String fileName) {
        if (bucketName == null || fileName == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Bucket name and file name must be provided").build();
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .getObjectRequest(getObjectRequest)
                .build();

        String signedUrl = presigner.presignGetObject(presignRequest).url().toString();

        return Response.ok().entity(signedUrl).build();
    }

}