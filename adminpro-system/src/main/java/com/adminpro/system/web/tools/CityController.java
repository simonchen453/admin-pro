package com.adminpro.system.web.tools;

import com.adminpro.framework.base.entity.R;
import com.adminpro.system.core.common.web.BaseController;
import com.adminpro.system.rbac.domains.entity.city.CityEntity;
import com.adminpro.system.rbac.domains.entity.city.CityService;
import com.adminpro.system.rbac.domains.vo.city.CityVo;
import com.adminpro.system.rbac.domains.vo.city.CityVoConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "城市区划管理", description = "省市区街道四级区划查询接口")
@RestController
@RequestMapping("/city")
/**
 * 使用 Lombok @RequiredArgsConstructor 自动生成构造器进行依赖注入。
 * 所有 final 字段将通过构造器自动注入，无需显式编写 @Autowired。
 * 添加新依赖时，只需添加 private final 字段即可。
 */
@RequiredArgsConstructor
public class CityController extends BaseController {

    private final CityService cityService;
    private final CityVoConverter cityVoConverter;

    /**
     * 查询省一级区划
     *
     * @return
     */
    @Operation(summary = "查询省一级区划", description = "获取所有省级行政区划列表")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 List<CityVo> 列表
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @RequestMapping(value = "/provinces", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<List<CityVo>> provinces() {
        List<CityEntity> byLevel = cityService.findByLevel(CityEntity.PROVINCE_LEVEL);
        List<CityVo> convert = cityVoConverter.convert(byLevel);
        return R.ok(convert);
    }

    /**
     * 查询省下面市一级区划
     *
     * @param provinceid
     * @return
     */
    @Operation(summary = "查询省下面市一级区划", description = "根据省ID查询该省下面的所有市级行政区划")
    @ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 List<CityVo> 列表
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @RequestMapping(value = "/province/{provinceid}/cities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<List<CityVo>> cities(@PathVariable String provinceid) {
        List<CityEntity> byLevel = cityService.findByLevelAndParent(CityEntity.CITY_LEVEL, provinceid);
        List<CityVo> convert = cityVoConverter.convert(byLevel);
        return R.ok(convert);
    }

    /**
     * 查询市下面区一级区划
     *
     * @param cityid
     * @return
     */
    @Operation(summary = "查询市下面区一级区划", description = "根据市ID查询该市下面的所有区县级行政区划")
    @ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 List<CityVo> 列表
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @RequestMapping(value = "/city/{cityid}/districts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<List<CityVo>> districts(@PathVariable String cityid) {
        List<CityEntity> byLevel = cityService.findByLevelAndParent(CityEntity.DISTRICT_LEVEL, cityid);
        List<CityVo> convert = cityVoConverter.convert(byLevel);
        return R.ok(convert);
    }

    /**
     * 查询区下面街道一级区划
     *
     * @param districtsid
     * @return
     */
    @Operation(summary = "查询区下面街道一级区划", description = "根据区ID查询该区下面的所有街道级行政区划")
    @ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 List<CityVo> 列表
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @RequestMapping(value = "/district/{districtsid}/street", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<List<CityVo>> street(@PathVariable String districtsid) {
        List<CityEntity> byLevel = cityService.findByLevelAndParent(CityEntity.STREET_LEVEL, districtsid);
        List<CityVo> convert = cityVoConverter.convert(byLevel);
        return R.ok(convert);
    }

    /**
     * 查询具体ID区划
     *
     * @param id
     * @return
     */
    @Operation(summary = "查询具体ID区划", description = "根据区划ID查询具体的行政区划信息")
    @ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含 CityVo 对象
                - restCode=500: ID不存在或服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<CityVo> city(@PathVariable String id) {
        CityEntity cityEntity = cityService.findCityById(id);
        if (cityEntity != null) {
            CityVo cityVo = new CityVo();
            cityVo.setId(cityEntity.getId());
            cityVo.setTitle(cityEntity.getTitle());
            cityVo.setParent(cityEntity.getParent());
            cityVo.setLevel(cityEntity.getLevel());
            cityVo.setKeyword(cityEntity.getKeyword());
            return R.ok(cityVo);
        } else {
            return R.error("ID不存在");
        }
    }

    /**
     * 查询具体ID区划
     *
     * @param address
     * @param certNo
     * @return
     */
    @Operation(summary = "根据地址和证件号查询区划ID", description = "解析地址字符串并返回省市区ID数组，结合身份证号前6位进行区划匹配")
    @ApiResponse(
        responseCode = "200",
        description = """
                统一响应格式，通过 restCode 判断业务状态：
                - restCode=200: 查询成功，data 字段包含省市区ID和详细地址的数组
                - restCode=500: 服务器内部错误
                """,
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = R.class))
    )
    @RequestMapping(value = "/ids", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<String[]> city(@RequestParam String address, @RequestParam String certNo) {
        String[] rs = new String[4];// 省、市、区、地址
        String[] strings = parseAddress(address);
        String province = strings[0];
        String city = strings[1];
        String district = strings[2];
        String addressDetail = strings[3];
        rs[3] = addressDetail;

        CityEntity cityEntity = cityService.findCityByTitle(province);
        if (cityEntity != null && cityEntity.getLevel() == CityEntity.PROVINCE_LEVEL) {
            rs[0] = cityEntity.getId();
            CityEntity cityEntity2 = cityService.findCityByTitle(city);
            if (cityEntity2 != null && cityEntity2.getLevel() == CityEntity.CITY_LEVEL) {
                rs[1] = cityEntity2.getId();
                CityEntity cityEntity3 = cityService.findDistrictByTitle(district, cityEntity2);
                if (cityEntity3 != null && cityEntity3.getLevel() == CityEntity.DISTRICT_LEVEL) {
                    rs[2] = cityEntity3.getId();
                } else {
                    return R.ok(handleCertNo(certNo, addressDetail));
                }
            } else if (cityEntity2 != null && cityEntity2.getLevel() == CityEntity.DISTRICT_LEVEL) {
                rs[2] = cityEntity2.getId();
                String parent = cityEntity2.getParent();
                CityEntity cityEntity3 = cityService.findCityById(parent);
                if (cityEntity3 != null && cityEntity3.getLevel() == CityEntity.CITY_LEVEL) {
                    rs[1] = cityEntity3.getId();
                    rs[3] = strings[2] + strings[3];
                } else {
                    return R.ok(handleCertNo(certNo, addressDetail));
                }
            } else {
                return R.ok(handleCertNo(certNo, addressDetail));
            }
        } else {
            return R.ok(handleCertNo(certNo, addressDetail));
        }
        return R.ok(rs);
    }

    private static String[] parseAddress(String address) {
        String province = "";
        String city = "";
        String district = "";
        // var address = "";
        String[] split1 = address.split("省");
        if (split1.length == 2) {
            province = split1[0] + "省";
            address = split1[1];
            String[] split2 = address.split("市");
            if (split2.length >= 2) {
                city = split2[0] + "市";
                address = address.replace(city, "");

                String[] split4 = address.split("市");
                if (split4.length == 2) {
                    district = split4[0] + "市";
                    address = split4[1];
                } else {
                    String[] split5 = address.split("县");
                    if (split5.length == 2) {
                        district = split5[0] + "县";
                        address = split5[1];
                    } else {
                        String[] split3 = address.split("区");
                        if (split3.length >= 2) {
                            district = split3[0] + "区";
                            address = address.replace(district, "");
                        }
                    }
                }
            }
        }

        return new String[] { province, city, district, address };
    }

    private String[] handleCertNo(String certNo, String detail) {
        String substring = certNo.substring(0, 6);
        CityEntity cityByid = cityService.findCityById(substring);
        String[] rs = new String[4];
        rs[3] = detail;
        if (cityByid != null && cityByid.getLevel() == CityEntity.DISTRICT_LEVEL) {
            rs[2] = cityByid.getId();
            rs[1] = cityByid.getParent();
            CityEntity cityByid1 = cityService.findCityById(cityByid.getParent());
            if (cityByid1 != null && cityByid1.getLevel() == CityEntity.CITY_LEVEL) {
                rs[0] = cityByid1.getParent();
            }
        }
        return rs;
    }
}
