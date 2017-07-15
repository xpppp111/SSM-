package com.atguigu.crud.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.atguigu.crud.bean.Employee;
import com.atguigu.crud.bean.Msg;
import com.atguigu.crud.service.EmployeeService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx.Binding;

/**
 * 处理员工 CRUD请求
 * @author wp
 *
 */
@Controller
public class EmployeeController {
	
	 
	@Autowired
	EmployeeService  employeeService;
	
	/**
	 * 单个批量二合一
	 * 如果批量:1-2-3
	 * 单个删除:1
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emp/{ids}",method=RequestMethod.DELETE)
	public Msg deleteEmpById(@PathVariable("ids")String ids){
		System.out.println("删除");
		//批量删除
		if(ids.contains("-")){
			System.out.println("多个删除");
			List<Integer> del_ids = new ArrayList<>();
			 String[] str_ids = ids.split("-");
			 //组装id的集合
			 for(String string : str_ids){
				del_ids.add(Integer.parseInt(string));
			 }
			 employeeService.deleteBatch(del_ids);
		}else{
			System.out.println("单个删除");
			Integer id = Integer.parseInt(ids);
			employeeService.deleteEmp(id);
		}
		return Msg.success();
	}
	
	/**
	 * 如果直接发送ajax=PUT形式的请求
	 * 封装的数据
	 * 除路径上的empId其他的全为Null
	 * 问题:
	 * 请求体中有数据:
	 * 但是Employee对象封装不上
	 *  update tbl_emp    where emp_id = 1014 
	 *  
	 *  原因:Tomcat:
	 *  1.将请求体中的数据,封装一个map.
	 *  2.request.getparameter("empName")就会从这个map中取值
	 *  3.SpringMVC封装POJO对象的时候
	 *  	会把POJO中中每个属性的值调用request.getparameter("email);
	 *  AJAX发送PUT请求引发的血案
	 *  PUT请求，请求体中的数据,request.getparameter("email),拿不到数据
	 *  Tomcat一看是PUT请求不会封装请求体中的数据为map,只有POST形式的请求才封装请求体为map
	 *  org.apache.catalina.connector.Request ; 
	 *  protected String parseBodyMethods = "POST"
	 *  if(!getConnector().isParseBodyMethod(getMethod())){
	 *  		success = true;
	 *  		return ;
	 *  }
	 *  
	 *   
	 *  
	 */
	
	/**解决方案 
	 * 我们要能支持直接发送PUT之类的请求还要封装请求体中的数据
	 * 1.配上HttpPutFormContentFilter
	 * 2.他的作用:将请求提中的数据解析包装成一个map. request被重新包装
	 * 3.request被重新包装:request.getparameter被()重写,就会从自己封装的map中取数据
	 * 员工更新方法
	 * @param employee
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/emp/{empId}",method=RequestMethod.PUT)
	public Msg saveEmp(Employee employee,HttpServletRequest request){
		System.out.println(request.getParameter("gender"));
		System.out.println(employee);
		employeeService.updateEmp(employee);
		return Msg.success();
	}
	
	
	/**
	 * 根据id查询员工
	 * @param id
	 * @return
	 */		
	@RequestMapping(value="/emp/{id}",method=RequestMethod.GET)
	@ResponseBody
	public Msg getEmp(@PathVariable("id")Integer id){
		System.out.println("进入");
		Employee employee = employeeService.getEmp(id);
		return Msg.success().add("emp", employee);
	}
	
	/**
	 * 检查用户名是否可用
	 * @param empName
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/checkuser")
	public Msg checkuse(@RequestParam("empName")String empName){
		//先判断用户名是否是合法的表达式
		String regx="(^[a-zA-Z0-9_-]{6,16}$)|(^[\u2E80-\u9FFF]{2,5})";
		
		if(!(empName.matches(regx))){
			System.out.println("不可用");
			return Msg.fail().add("va_msg","用户名必须是6-16位数字和字母的组合或者2-5位中文");
		}
		
		//数据库用户名重复校验
		boolean b =employeeService.checkUser(empName);
		if(b){
			return Msg.success();
		}else{
			return Msg.fail().add("va_msg", "用户名不可用");
		}
	
	}
	
	/**
	 * 员工保存
	 * @param employee
	 * @return
	 * 1.支持JSR303校验
	 * 2.导入Hibernate-Validator
	 */
	 
	@RequestMapping(value="emp",method=RequestMethod.POST) //定义为POST请求
	@ResponseBody
	public Msg saveEmp(@Valid Employee employee,BindingResult result){
		if(result.hasErrors()){
			Map<String,Object> map= new HashMap<>();
			//校验失败，应该返回失败，在模态框中显示校验失败的信息
			List<FieldError> errors = result.getFieldErrors(); //提取出错误信息
			for(FieldError fieldError : errors){
				System.out.println("错误的字段名:"+ fieldError.getField());  
				System.out.println("错误信息:"+fieldError.getDefaultMessage());
				map.put(fieldError.getField(), fieldError.getDefaultMessage());
			}
			return Msg.fail().add("errorFields", map);
			
		}else{
			employeeService.saveEmp(employee); 
			return Msg.success();
		}
	 
	}
	
	/**
	 * 导入jackson包
	 * @param pn
	 * @param model
	 * @return
	 */
	@RequestMapping("/emps")
	//自动将返回的对象转换为json字符串
	@ResponseBody
	public Msg getEmpsWithJson(@RequestParam(value="pn" ,defaultValue="1")Integer pn,Model model){
				//引入PageHelper分页插件
				//在查询之前只需要调用,传入页码，以及分页每一页的大小
				PageHelper.startPage(pn, 5);
				//startPage后面紧跟的这个查询就是一个分页查询 
				List<Employee> emps =employeeService.getAll();
				//使用pageInfo包装查询后的信息,只需要将pageinfo交给页面就行了。
				//封装了详细的分页信息,包括有我们查询出来的数据,传入:连续显示的页数
				PageInfo page=new PageInfo(emps,5); //5表示要连续显示的页数
				return Msg.success().add("pageInfo",page);
		
		
	}
	
	/**
	 * 查询员工数据(分页查询)
	 * @return
	 */
	
	///@RequestMapping("/emps")
	public String getEmps(@RequestParam(value="pn" ,defaultValue="1")Integer pn,Model model){ //传入当前页面，如果没有传则默认为1
		
		//引入PageHelper分页插件
		//在查询之前只需要调用,传入页码，以及分页每一页的大小
		PageHelper.startPage(pn, 5);
		//startPage后面紧跟的这个查询就是一个分页查询 
		List<Employee> emps =employeeService.getAll();
		//使用pageInfo包装查询后的信息,只需要将pageinfo交给页面就行了。
		//封装了详细的分页信息,包括有我们查询出来的数据,传入:连续显示的页数
		PageInfo page=new PageInfo(emps,5); //5表示要连续显示的页数
		model.addAttribute("PageInfo",page); 		
		return "list";
		
	}

}
