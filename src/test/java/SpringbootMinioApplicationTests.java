import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.minio.messages.VersioningConfiguration;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author liuming
 * @description
 * @date 2023/1/12
 */
@SpringBootTest
public class SpringbootMinioApplicationTests {
    private static MinioClient minioClient;
    private static String bucketName = "text01";
    private static String filePath = "C:\\Users\\liuming\\Desktop\\878898.png";
    private static String endPoint = "http://101.43.166.26:9000";
    private static String accessKey = "minioadmin";
    private static String secretKey = "minioadmin";
    //上传文件大小限制5M
    private static long limitSize = 5242880L;

    private void init() {
        minioClient = MinioClient
                .builder()
                .endpoint(endPoint)
                .credentials(accessKey, secretKey)
                .build();

    }

    /**
     * 版本控制
     *
     * @throws Exception
     */
    private void version() throws Exception {
        init();
        // Enable versioning on 'my-bucketname'.
        minioClient.setBucketVersioning(
                SetBucketVersioningArgs.builder()
                        .bucket(bucketName)
                        .config(new VersioningConfiguration(VersioningConfiguration.Status.ENABLED, null))
                        .build());
        System.out.println("Bucket versioning is enabled successfully");
//
//        // Suspend versioning on 'my-bucketname'.
//        minioClient.setBucketVersioning(
//                SetBucketVersioningArgs.builder()
//                        .bucket("my-bucketname")
//                        .config(new VersioningConfiguration(VersioningConfiguration.Status.SUSPENDED, null))
//                        .build());
//        System.out.println("Bucket versioning is suspended successfully");
    }

    /**
     * 新建桶
     *
     * @throws Exception
     */
    @Test
    public void testCreateBucket() throws Exception {
        init();
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (isExist) {
            System.out.println(bucketName + "已经存在！");
        } else {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            System.out.println("创建了一个名字是" + bucketName + "的bucket");
        }
    }

    /**
     * 查询桶列表
     *
     * @throws Exception
     */
    @Test
    public void testListBuckets() throws Exception {
        init();
        List<Bucket> bucketList = minioClient.listBuckets();
        bucketList.forEach(p -> {
            System.out.println(p.name());
        });
    }

    /**
     * 上传
     *
     * @throws Exception
     */
    @Test
    public void testUploadFile() throws Exception {
        init();
        version();
        for (int i = 0; i < 10; i++) {
            minioClient.uploadObject(
                    UploadObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object("text.jpg")//上传的名
                            .filename(filePath)//本地磁盘地址
                            .build());
        }


        System.out.println("上传完毕，请刷新minio的web页面，查看上传文件");
    }

    /**
     * 下载
     *
     * @throws Exception
     */
    @Test
    public void testDownloadFile() throws Exception {
        init();
        minioClient.downloadObject(
                DownloadObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object("text.jpg")//minio上的文件名
                        .filename("C:\\Users\\liuming\\Desktop\\text.jpg")//下载的文件名
                        .build()
        );
        System.out.println("下载完毕");
    }


    /**
     * 删除
     *
     * @throws Exception
     */
    @Test
    public void testRemoveBucket() throws Exception {
        init();
        boolean isExist = minioClient.bucketExists(
                BucketExistsArgs
                        .builder()
                        .bucket(bucketName)
                        .build()
        );
        if (isExist) {
            //桶不空，删不掉，所以清桶的objects
            Iterable<Result<Item>> iterable = minioClient.listObjects(
                    ListObjectsArgs
                            .builder()
                            .bucket(bucketName)
                            .build()
            );
            for (Result<Item> o : iterable) {
                System.out.println("当前objectname---->>>>>>>" + o.get().objectName());
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(o.get().objectName())
                                .versionId(o.get().versionId())
                                .build());
                System.out.println("清理---->>>>>>>" + o.get().objectName());
            }
            System.out.println("清理" + bucketName + "下的object完毕");
            minioClient.removeBucket(
                    RemoveBucketArgs
                            .builder()
                            .bucket(bucketName)
                            .build()
            );
            if (!minioClient.bucketExists(
                    BucketExistsArgs.
                            builder()
                            .bucket(bucketName)
                            .build()
            )) {
                System.out.println("删除" + bucketName + "完毕，刷新minio的web页面");
            }
        } else {
            System.out.println("没有这个bucket，无需操作");
        }
    }

}
