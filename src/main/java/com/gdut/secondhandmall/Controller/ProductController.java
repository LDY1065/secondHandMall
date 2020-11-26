package com.gdut.secondhandmall.Controller;

import com.alibaba.fastjson.JSON;

import com.gdut.secondhandmall.Do.PmsProductOfflineDO;
import com.gdut.secondhandmall.Do.PmsProductOffshelvesDO;
import com.gdut.secondhandmall.Do.PmsProductOnlineDO;
import com.gdut.secondhandmall.Do.PmsProductVertifyRecordDO;
import com.gdut.secondhandmall.Dto.*;
import com.gdut.secondhandmall.Service.*;
import com.gdut.secondhandmall.Util.ConstanForProduct;
import com.gdut.secondhandmall.Util.IDGenerator;
import com.gdut.secondhandmall.Util.TransferUtilForProduct;
import com.gdut.secondhandmall.Result.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author GGXian
 * @project secondhandmall
 * @createTIme 2020/8/9-20:10
 * @description
 **/
@RestController
@RequestMapping("/productService")
public class ProductController {
    @Autowired
    PmsProductCategoryService categoryService;
    @Autowired
    PmsProductVertifyRecordService vertifyRecordService;
    @Autowired
    PmsProductOffshelvesService offshelvesService;
    @Autowired
    PmsProductOfflineService offlineService;
    @Autowired
    PmsProductOnlineService onlineService;
    @Autowired
    PictureService pictureService;
    @Autowired
    RedisService redisService;
    @Autowired
    ESService esService;
    @Autowired
    IDGenerator idGenerator;
    @Autowired
    TransferUtilForProduct transferUtilForProduct;

    /**
     * 获取新商品的id
     * @return
     */
    @GetMapping("/productId")
    public CommonResult<Long> getProductId(){
        return CommonResult.success(idGenerator.nextId());
    }
    /**
     * 提交新商品
     * @param userId
     * @param
     * @return
     */
    @PostMapping("/newProduct/{userId}")
    public CommonResult publish(@PathVariable String userId, @RequestBody PmsProductVertifyRecordDO vertifyRecord){
        ProductPictureInUrlDTO pictures = JSON.parseObject(vertifyRecord.getPicUrl(), ProductPictureInUrlDTO.class);
        //构造审核记录对象
        vertifyRecord.setOpenid(userId);
        vertifyRecord.setCreateTime(new Date());
        vertifyRecord.setProductId(vertifyRecord.getProductId());
        vertifyRecord.setStatus(0);
        vertifyRecord.setStatus(new Byte(String.valueOf(ConstanForProduct.WAITING_FOR_VERIFING)));
        vertifyRecord.setPicUrl(JSON.toJSONString(pictures));
        //放进数据库中
        vertifyRecordService.insertNewRecord(vertifyRecord);
        //返回成功
        return CommonResult.success();
    }

    /**
     * 处理要上传的图片，并返回生成的url数组
     * @param pictures
     * @return
     */
    @PostMapping("/pictures/{productId}")
    public CommonResult<ProductPictureInUrlDTO> postPictures(@Nullable ProductPictureInFileDTO pictures,
                                                            @PathVariable long productId){
        boolean hasMain = false;
        MultipartFile main = null;
        List<MultipartFile> detail = null;
        String fileName = null;
        LinkedHashMap<InputStream, String> streamMap = new LinkedHashMap<>();
        List<String> urlList = null;
        ProductPictureInUrlDTO urls = new ProductPictureInUrlDTO();
        if (pictures == null){
            return CommonResult.success(new ProductPictureInUrlDTO());
        }
        //将图片存在服务器中，将路径存储在数据库中
        if (pictures.getMain() != null){
            main = pictures.getMain();
            hasMain = true;
            fileName = main.getOriginalFilename();
            try {
                streamMap.put(main.getInputStream(), fileName.substring(fileName.lastIndexOf('.')));
            } catch (IOException e) {
                e.printStackTrace();
                return CommonResult.failed();
            }
        }
        if ((detail = pictures.getDetail()) != null){
            for (MultipartFile file : detail) {
                fileName = file.getOriginalFilename();
                try {
                    streamMap.put(file.getInputStream(), fileName.substring(fileName.lastIndexOf('.')));
                } catch (IOException e) {
                    e.printStackTrace();
                    return CommonResult.failed();
                }
            }
        }
        urlList = pictureService.storeToLocal(streamMap, productId);
        if (hasMain){
            System.out.println(urlList.size());
            urls.setMain(urlList.get(0));
            urls.setDetail(urlList.subList(1, urlList.size()));
            return CommonResult.success(urls);
        }
        urls.setDetail(urlList);
        return CommonResult.success(urls);
    }

    /**
     * 查询二级目录
     * @param primaryDirectory
     * @return
     */
    @GetMapping("/category/{primaryDirectory}")
    public CommonResult<List<String>> getSecondaryDirectories(@PathVariable String primaryDirectory){
        //查询并返回二级目录列表
        return CommonResult.success(categoryService.getSecondaryDirectoryByPrimaryDirectory(primaryDirectory));
    }

    /**
     * 下架商品
     * @param productId
     * @param offshelvesDO
     * @return
     */
    @DeleteMapping("/productWithdrawal/{productId}")
    public CommonResult putOffShelves(@PathVariable long productId, @RequestBody PmsProductOffshelvesDO offshelvesDO){
        offshelvesDO.setProductId(productId);
        if (!offshelvesService.putOffShelves(offshelvesDO)){
            return CommonResult.failed();
        }
        return CommonResult.success();
    }

    /**
     * 获取所有已售商品
     * @param pageParam
     * @return
     */
    @GetMapping("/productsSold")
    public CommonResult<List<PmsProductOfflineDO>> getProductsSold(PageParamDTO pageParam){
        List<PmsProductOfflineDO> productsSold = offlineService.getProductsSold(pageParam);
        if(productsSold == null){
            return CommonResult.failed();
        }
        return CommonResult.success(productsSold);
    }

    /**
     * 获取所有在售商品
     * @param pageParam
     * @return
     */
    @GetMapping("/productsForSale")
    public CommonResult<List<PmsProductOnlineDO>> getProductsForSale(PageParamDTO pageParam){
        System.out.println(pageParam);
        List<PmsProductOnlineDO> productsForSale = onlineService.getProductsForSale(pageParam);
        if(productsForSale == null){
            return CommonResult.failed();
        }
        return CommonResult.success(productsForSale);
    }

    /**
     * 获取某用户的所有已售商品信息
     * @param sessionKey
     * @param pageParam
     * @return
     */
    @GetMapping("/productsSold/{sessionKey}")
    public CommonResult<List<PmsProductOfflineDO>> getProductsSoldForUser(@PathVariable String sessionKey, PageParamDTO pageParam){
        List<PmsProductOfflineDO> productsSoldForUser = offlineService.getProductsSoldForUser(pageParam, sessionKey);
        if(productsSoldForUser == null){
            return CommonResult.failed();
        }
        return CommonResult.success(productsSoldForUser);
    }

    /**
     * 获取某用户的说有在售商品信息
     * @param sessionKey
     * @param pageParam
     * @return
     */
    @GetMapping("/productsForSale/{sessionKey}")
    public CommonResult<List<PmsProductOnlineDO>> getProductsForSaleForUser(@PathVariable String sessionKey, PageParamDTO pageParam){
        List<PmsProductOnlineDO> productsForSaleForUser = onlineService.getProductsForSaleForUser(pageParam, sessionKey);
        if(productsForSaleForUser == null){
            return CommonResult.failed();
        }
        return CommonResult.success(productsForSaleForUser);
    }

    /**
     * 获取全部商品审核记录
     * @param pageParam
     * @return
     */
    @GetMapping("/vertifyRecord")
    public CommonResult<List<PmsProductVertifyRecordDO>> getVertifyRecords(PageParamDTO pageParam){
        List<PmsProductVertifyRecordDO> vertifyRecords = vertifyRecordService.getVertifyRecords(pageParam);
        if(vertifyRecords == null){
            return CommonResult.failed();
        }
        return CommonResult.success(vertifyRecords);
    }

    /**
     * 获取某用户的商品审核记录
     * @param sessionKey
     * @param pageParam
     * @return
     */
    @GetMapping("/vertifyRecord/{sessionKey}")
    public CommonResult<List<PmsProductVertifyRecordDO>> getVertifyRecordsForUser(@PathVariable String sessionKey, PageParamDTO pageParam){
        List<PmsProductVertifyRecordDO> vertifyRecordsForUser = vertifyRecordService.getVertifyRecordsForUser(pageParam, sessionKey);
        if(vertifyRecordsForUser == null){
            return CommonResult.failed();
        }
        return CommonResult.success(vertifyRecordsForUser);
    }

    /**
     * 获取某商品的详细审核记录
     * @param productId
     * @return
     */
    @GetMapping("/verifyRecordDetail/{productId}")
    public CommonResult<PmsProductVertifyRecordDO> getVerifyRecordDetail(@PathVariable long productId){
        PmsProductVertifyRecordDO verifyRecordDetail = vertifyRecordService.getVerifyRecordDetail(productId);
        if(verifyRecordDetail == null){
            return CommonResult.failed();
        }
        return CommonResult.success(verifyRecordDetail);
    }

    /**
     * 获取某在售商品的详细信息
     * @param id
     * @return
     */
    @GetMapping("/product/{id}")
    public CommonResult<PmsProductOnlineDO> getProductByProductId(@PathVariable long id, @RequestBody String openId){
        PmsProductOnlineDO product = onlineService.getProductByProductId(id, openId);
        if(product == null){
            return CommonResult.failed();
        }
        return CommonResult.success(product);
    }

    /**
     * 获取某二级目录下的商品
     * @param secondaryDirectory
     * @param
     * @param pageParam
     * @return
     */
    @GetMapping("/productsForType/{secondaryDirectory}")
    public CommonResult<List<ProductEssentialsDTO>> getProductByDirectoryId(@PathVariable short secondaryDirectory,
                                                                            SortParamDTO sortParam, PageParamDTO pageParam){
        List<ProductEssentialsDTO> byTag = esService.getByTag(secondaryDirectory, sortParam, pageParam);
        if (byTag == null){
            return CommonResult.failed();
        }
        return CommonResult.success(byTag);
    }

    /**
     * 按关键字搜索商品
     * @param keyword
     * @param sortParam
     * @param pageParam
     * @return
     */
    @GetMapping("/productSearched/{keyword}")
    public CommonResult getProductByKeyword(@PathVariable String keyword, @RequestBody SortParamDTO sortParam,
                                            @RequestBody PageParamDTO pageParam){
        List<ProductEssentialsDTO> search = esService.search(keyword, sortParam, pageParam);
        if (search == null){
            return CommonResult.failed();
        }
        return CommonResult.success(search);
    }

    /**
     * 完成商品审核
     * @param productId
     * @param verification
     * @return
     */
    @PutMapping("/verification/{productId}")
    public CommonResult completeVerification(@PathVariable long productId, @RequestBody VerificationDTOForProduct verification){
        System.out.println(verification);
        verification.setProductId(productId);
        if (!vertifyRecordService.completeVerification(verification)){
            return CommonResult.failed();
        }
        return CommonResult.success();
    }

    /**
     * 获取人气商品
     * @return
     */
    @GetMapping("/bestSellers")
    public CommonResult<List<ProductEssentialsDTO>> getBestSellers(){
        List<ProductEssentialsDTO> bestSeller = redisService.getBestSeller();
        if (bestSeller == null){
            return CommonResult.failed();
        }
        return CommonResult.success(bestSeller);
    }

    /**
     * 修改商品信息
     * @param productId
     * @param modification
     * @return
     */
    @PatchMapping("/productModification/{productId}")
    public CommonResult productModification(@PathVariable long productId,
                                            @RequestBody ProductModificationDTO modification){
        //将传输的数据放入模型中
        PmsProductOnlineDO newProduct = new PmsProductOnlineDO();
        newProduct.setProductId(productId);
        newProduct.setProductName(modification.getProductName());
        newProduct.setDescription(modification.getDescription());
        newProduct.setDirectoryId(modification.getDirectoryId());
        newProduct.setOriginalPrice(modification.getOriginalPrice());
        newProduct.setPrice(modification.getPrice());
        System.out.println("upload:" + modification.getPicturesToUpload());
        System.out.println("delete:" + modification.getPicturesToDelete());
        String[] urls = modification.getPicturesToDelete().split(";");
        List<String> picturesToDelete = Arrays.asList(urls);
        ProductPictureInUrlDTO picturesToUpload = JSON.parseObject(modification.getPicturesToUpload(),
                ProductPictureInUrlDTO.class);
        ProductPictureInUrlDTO newPictures = null;
        PmsProductOnlineDO oldProduct = null;
        List<String> newDetail = null;

        //删除图片
        if (picturesToDelete != null){
            if (!pictureService.deletePictures(picturesToDelete, productId)){
                return CommonResult.failed();
            }
        }
        oldProduct = onlineService.getProductByProductId(productId, modification.getOpenid());
        newProduct.setOpenid(oldProduct.getOpenid());
        newProduct.setPicUrl(oldProduct.getPicUrl());
        newProduct.setVisitor(oldProduct.getVisitor());
        newProduct.setTime(oldProduct.getTime());
        System.out.println("oldProduct:" + oldProduct);
        //增加图片
        if (picturesToUpload != null){
            newPictures = JSON.parseObject(oldProduct.getPicUrl(), ProductPictureInUrlDTO.class);
            if (picturesToUpload.getMain() != null){
                newPictures.setMain(picturesToUpload.getMain());
            }
            newDetail = newPictures.getDetail();
            for (String url : picturesToUpload.getDetail()) {
                newDetail.add(url);
            }
            newPictures.setDetail(newDetail);
            newProduct.setPicUrl(JSON.toJSONString(newPictures));
        }
        //修改mysql、修改redis
        System.out.println("newProduct:" + newProduct);
        if (!onlineService.updateProductByProductId(newProduct)){
            return CommonResult.failed();
        }
        //修改ES
        if (!esService.update(transferUtilForProduct.transferOnlineToEssential(newProduct))){
            return CommonResult.failed();
        }
        return CommonResult.success();
    }
}
