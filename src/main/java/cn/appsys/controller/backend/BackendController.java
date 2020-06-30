package cn.appsys.controller.backend;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.jdbc.StringUtils;

import cn.appsys.pojo.AppCategory;
import cn.appsys.pojo.AppInfo;
import cn.appsys.pojo.DataDictionary;
import cn.appsys.service.backend.AppService;
import cn.appsys.service.backend.BackendUserService;
import cn.appsys.service.developer.AppCategoryService;
import cn.appsys.service.developer.DataDictionaryService;
import cn.appsys.tools.Constants;
import cn.appsys.tools.PageSupport;

@Controller
@RequestMapping(value="/admin/backend")
public class BackendController {
	private Logger logger = Logger.getLogger(BackendController.class);
	@Resource
	private AppService appServie;
	@Resource
	private DataDictionaryService dataDictionaryService;
	@Resource
	private AppCategoryService appCategoryService;
	/**
	 * 跳转APP列表 
	 */
	@RequestMapping(value="/applist")
	public String AppPage(Model modle,
			@RequestParam(value="querySoftwareName",required = false) String querySoftwareName,
			@RequestParam(value="queryCategoryLevel1",required= false) String queryCategoryLevel1,
			@RequestParam(value="queryCategoryLevel2",required=false) String queryCategoryLevel2,
			@RequestParam(value="queryCategoryLevel3",required=false) String queryCategoryLevel3,
			@RequestParam(value="queryFlatformId",required=false) String queryFlatformId,
			@RequestParam(value="pageIndex",required=false) String pageIndex,
			HttpServletRequest request){
		logger.info("============"+queryFlatformId);
		//数据总条数
		int totalCount = 0;
		//存储未审核app的列表
		List<AppInfo> appList = null;
		//存储平台信息列表
		List<DataDictionary> DataDictionaryList = null;
		//存储一级查询信息
		List<AppCategory> categoryList = null; 
		//存储二级查询列表
		List<AppCategory> category2 = null; 
		//存储三级查询列表
		List<AppCategory> category3 = null; 
		//一级查询
		Integer _queryCategoryLevel1 = null;
		if(queryCategoryLevel1 != null && !queryCategoryLevel1.equals("")) {
			_queryCategoryLevel1 = Integer.parseInt(queryCategoryLevel1);
		}
		//二级查询
		Integer _queryCategoryLevel2 = null;
		if(queryCategoryLevel2 != null && !queryCategoryLevel2.equals("")) {
			_queryCategoryLevel2 = Integer.parseInt(queryCategoryLevel2);
		}
		//三级查询 
		Integer _queryCategoryLevel3 = null;
		if(queryCategoryLevel3 != null && !queryCategoryLevel3.equals("")) {
			_queryCategoryLevel3 = Integer.parseInt(queryCategoryLevel3);
		}
		//所属平台查询
		Integer _queryFlatformId = null;
		if(queryFlatformId != null && !queryFlatformId.equals("")) {
			_queryFlatformId = Integer.parseInt(queryFlatformId);
		}
		//名称查询
		if(null == querySoftwareName) {
			querySoftwareName = "";
		}
		//初始化当前页面
		int _currentPageNo = 1;
		if(pageIndex != null && !pageIndex.equals("")) {
			_currentPageNo = Integer.parseInt(pageIndex);
		}
		//初始化分页类
		PageSupport pages = new PageSupport();
		//设置每页显示容量
		int pageSize = Constants.pageSize;
		try {
			//获得总数量
			totalCount = appServie.
					getAppInfoCount(querySoftwareName, _queryCategoryLevel1,
							_queryCategoryLevel2, _queryCategoryLevel3,
							_queryFlatformId);
			//设置每页容量
			pages.setPageSize(pageSize);
			//设置数据总量 并计算出页面总数
			pages.setTotalCount(totalCount);
			//设置当前页码
			pages.setCurrentPageNo(_currentPageNo);
			//控制页码
			if(pages.getCurrentPageNo() < 1) {
				pages.setCurrentPageNo(1);
			}else if(pages.getCurrentPageNo() > pages.getTotalPageCount()) {
				pages.setCurrentPageNo(pages.getTotalPageCount());
			}
			//获取未审核app的列表
			logger.info(pages.getCurrentPageNo());
			appList = appServie.getAppInfoList(querySoftwareName, _queryCategoryLevel1,
					_queryCategoryLevel2, _queryCategoryLevel3, 
					_queryFlatformId,pages.getCurrentPageNo(), pageSize);
			//获取平台信息
			DataDictionaryList = dataDictionaryService.getDataDictionaryList("APP_FLATFORM");
			//获取一级查询列表
			categoryList = appCategoryService.getAppCategoryListByParentId(null);
			//获取二级查询列表
			category2 =
					appCategoryService.getAppCategoryListByParentId(_queryCategoryLevel1);
			for(AppCategory c : category2) {
				logger.info(c.getId());
			}
			//获取三级查询列表
			category3 =
					appCategoryService.getAppCategoryListByParentId(_queryCategoryLevel2);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		modle.addAttribute("pages",pages);
		modle.addAttribute("appInfoList",appList);
		modle.addAttribute("flatFormList", DataDictionaryList);
		
		request.setAttribute("querySoftwareName", querySoftwareName);
		request.setAttribute("queryFlatformId", _queryFlatformId);
		request.setAttribute("queryCategoryLevel1", _queryCategoryLevel1);
		request.setAttribute("queryCategoryLevel2", _queryCategoryLevel2);
		request.setAttribute("queryCategoryLevel3", _queryCategoryLevel3);
		request.setAttribute("categoryLevel1List", categoryList);
		
		if(null != queryCategoryLevel2 && !queryCategoryLevel2.equals("")) {
			request.setAttribute("categoryLevel2List", category2);
			request.setAttribute("categoryLevel3List", category3);
		}
		/*
		 * if(null != queryCategoryLevel3 && !queryCategoryLevel3.equals("")) {
		 * 
		 * }
		 */
		

		return "backend/applist";
	}
	/**
	 * 获取二级列表
	 */
	@RequestMapping(value="/queryCategoryLevel2.json",method=RequestMethod.GET)
	@ResponseBody 
	public List<AppCategory> queryCategoryLevel2(@RequestParam(value="pid") String id,
			HttpServletRequest request) { 
		logger.info("二级列表查询");
		List<AppCategory> category = null; 
		try { 
			Integer pid = Integer.parseInt(id);
			category =
					appCategoryService.getAppCategoryListByParentId(pid);
		}catch (Exception e){ 
			e.printStackTrace(); 
		} 
		//request.setAttribute("categoryLevel2List", category);
		return category; 
	}
	/**
	 * 获取三级列表
	 */
	@RequestMapping(value="/queryCategoryLevel3.json",method=RequestMethod.GET)
	@ResponseBody 
	public List<AppCategory> queryCategoryLevel3(@RequestParam(value="pid") String id) { 
		logger.info("三级列表查询");
		List<AppCategory> category = null; 
		try { 
			Integer pid = Integer.parseInt(id);
			category =
					appCategoryService.getAppCategoryListByParentId(pid);


		}catch (Exception e){ 
			e.printStackTrace(); 
		} 
		return category; 
	}
}
