package com.adminpro.web.tools;

import com.adminpro.core.base.entity.R;
import com.adminpro.rbac.domains.entity.city.CityEntity;
import com.adminpro.rbac.domains.entity.city.CityService;
import com.adminpro.rbac.domains.vo.city.CityVo;
import com.adminpro.rbac.domains.vo.city.CityVoConverter;
import org.javasimon.aop.Monitored;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/city")
@Monitored
public class CityController {

    @Autowired
    private CityService cityService;

    @Autowired
    private CityVoConverter cityVoConverter;

    /**
     * 查询省一级区划
     *
     * @return
     */
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
    @RequestMapping(value = "/ids", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public R<String[]> city(@RequestParam String address, @RequestParam String certNo) {
        String[] rs = new String[4];//省、市、区、地址
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

        return new String[]{province, city, district, address};
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
