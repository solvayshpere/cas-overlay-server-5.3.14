package com.solvay.cas.controller;

import com.solvay.cas.domain.ServiceDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apereo.cas.services.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * @author: solvay
 * @date: 2020/8/10
 * @description:
 */
@RestController
@RequestMapping("/service")
public class ServiceController {

    private Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    @Qualifier("servicesManager")
    private ServicesManager servicesManager;

    /**
     * 注册service
     *
     * @param serviceId 域名
     * @param id        顺序
     * @return
     */
    @RequestMapping(value = "/addClient/{serviceId}/{id}", method = RequestMethod.GET)
    public Object addClient(@PathVariable("serviceId") String serviceId, @PathVariable("id") int id) {
        try {
            String a = "^(https|imaps|http)://" + serviceId + ".*";
            RegexRegisteredService service = new RegexRegisteredService();
            ReturnAllAttributeReleasePolicy re = new ReturnAllAttributeReleasePolicy();
            service.setServiceId(a);
            service.setId(id);
            service.setAttributeReleasePolicy(re);
            service.setName("login");
            //这个是为了单点登出而作用的
            service.setLogoutUrl(new URL("http://" + serviceId));
            servicesManager.save(service);
            //执行load让他生效
            servicesManager.load();
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.setCode(200);
            returnMessage.setMessage("添加成功");
            return returnMessage;
        } catch (Exception e) {
            logger.error("注册service异常", e);
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.setCode(500);
            returnMessage.setMessage("添加失败");
            return returnMessage;
        }
    }

    /**
     * 删除service异常
     *
     * @param serviceId
     * @return
     */
    @RequestMapping(value = "/deleteClient/{serviceId}/{id}", method = RequestMethod.GET)
    public Object deleteClient(@PathVariable("serviceId") String serviceId, @PathVariable("id") int id) {
        try {
            String aa = "http://"+serviceId;
            RegisteredService service = servicesManager.findServiceBy(aa);
            try {
                servicesManager.delete(service);
            } catch (Exception e){
                //这里会报审计错误，直接进行捕获即可，不影响删除逻辑
                logger.error(e.getMessage());
            }
            //执行load生效
            servicesManager.load();

            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.setCode(200);
            returnMessage.setMessage("删除成功");
            return returnMessage;
        } catch (Exception e) {
            logger.error("删除service异常", e);
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.setCode(500);
            returnMessage.setMessage("删除失败");
            return returnMessage;
        }
    }

    public class ReturnMessage {

        private Integer code;

        private String message;

        private Object data;

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }


    @PostMapping("/service")
    public Object addService(@RequestBody ServiceDO service) throws Exception{
        try {
            RegisteredService registeredService = findByServiceId(service.getServiceId());
            if (registeredService != null) {
                ReturnMessage returnMessage = new ReturnMessage();
                returnMessage.setCode(500);
                returnMessage.setMessage("serviceId:" + service.getServiceId() + " 已存在");
                return returnMessage;
            }

            RegexRegisteredService regexRegisteredService = covertService(service);
            servicesManager.save(regexRegisteredService, true);
            servicesManager.load();

            registeredService = findByServiceId(service.getServiceId());
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.setCode(200);
            returnMessage.setMessage("添加成功");
            returnMessage.setData(covertRegisteredService(registeredService));
            return returnMessage;
        }catch (Exception e){
            logger.error("注册service异常", e);
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.setCode(500);
            returnMessage.setMessage("添加失败");
            return returnMessage;
        }
    }

    @DeleteMapping("/service")
    public Object delService(@RequestParam("serviceId") String serviceId){
        try {
            boolean flag = false;
            RegisteredService registeredService = findByServiceId(serviceId);
            if (registeredService != null) {
                try {
                    servicesManager.delete(registeredService);
                } catch (Exception e) {
                    //这里会报审计错误，直接进行捕获即可，不影响删除逻辑
                    logger.error(e.getMessage());
                }
                if (null == findByServiceId(serviceId)) {
                    servicesManager.load();
                    flag = true;
                }
            }else{
                ReturnMessage returnMessage = new ReturnMessage();
                returnMessage.setCode(500);
                returnMessage.setMessage("serviceId:" + serviceId + " 已不存在");
                return returnMessage;
            }

            if (flag){
                ReturnMessage returnMessage = new ReturnMessage();
                returnMessage.setCode(200);
                returnMessage.setMessage("删除成功");
                return returnMessage;
            }else{
                ReturnMessage returnMessage = new ReturnMessage();
                returnMessage.setCode(500);
                returnMessage.setMessage("删除失败");
                return returnMessage;
            }
        }catch (Exception e){
            logger.error("删除service异常", e);
            ReturnMessage returnMessage = new ReturnMessage();
            returnMessage.setCode(500);
            returnMessage.setMessage("删除失败");
            return returnMessage;
        }
    }

    @GetMapping("/all")
    public Object getAllService() {
        Collection<RegisteredService> allServices = servicesManager.getAllServices();
        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.setCode(200);
        returnMessage.setMessage("查询成功");
        returnMessage.setData(covertRegisteredServiceList(allServices));
        return returnMessage;
    }

    @GetMapping
    public Object getByServiceId(@RequestParam("serviceId") String serviceId) {
        RegisteredService service = findByServiceId(serviceId);
        ReturnMessage returnMessage = new ReturnMessage();
        returnMessage.setCode(200);
        returnMessage.setMessage("查询成功");
        returnMessage.setData(covertRegisteredService(service));
        return returnMessage;
    }

    private ServiceDO covertRegisteredService(RegisteredService registeredService) {
        ServiceDO service = new ServiceDO();

        service.setServiceId(registeredService.getServiceId());
        service.setDescription(registeredService.getDescription());
        service.setEvaluationOrder(registeredService.getEvaluationOrder());
        service.setId(registeredService.getId());
        service.setName(registeredService.getName());
        service.setTheme(registeredService.getTheme());

        return service;
    }

    private List<ServiceDO> covertRegisteredServiceList(Collection<RegisteredService> registeredServices) {
        if (CollectionUtils.isEmpty(registeredServices)) {
            return null;
        }
        List<ServiceDO> services = new ArrayList<>();
        for (RegisteredService registeredService : registeredServices) {
            services.add(covertRegisteredService(registeredService));
        }

        return services;
    }

    private RegexRegisteredService covertService(ServiceDO service) throws Exception {
        RegexRegisteredService regexRegisteredService = new RegexRegisteredService();

        String serviceId = "^(https|imaps|http)://" + service.getServiceId() + ".*";
        ReturnAllAttributeReleasePolicy returnAllAttributeReleasePolicy = new ReturnAllAttributeReleasePolicy();

        regexRegisteredService.setServiceId(serviceId);
        regexRegisteredService.setId(service.getId());
        regexRegisteredService.setDescription(service.getDescription());
        regexRegisteredService.setEvaluationOrder(service.getEvaluationOrder());
        if (StringUtils.isNotBlank(service.getTheme())) {
            regexRegisteredService.setTheme(service.getTheme());
        }
        regexRegisteredService.setAttributeReleasePolicy(returnAllAttributeReleasePolicy);
        regexRegisteredService.setName(service.getName());
        regexRegisteredService.setLogoutUrl(new URL("http://" + service.getServiceId()));

        return regexRegisteredService;
    }

    public RegisteredService findByServiceId(String serviceId){
        RegisteredService service = null;
        serviceId = "http://" + serviceId;
        try {
            service = servicesManager.findServiceBy(serviceId);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return service;
    }
}
