package com.fitanywhere.course.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.fitanywhere.course.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fitanywhere.ann.model.AnnService;
import com.fitanywhere.ann.model.AnnVO;
import com.fitanywhere.coach.model.CoachService;
import com.fitanywhere.coach.model.CoachVO;
import com.fitanywhere.coursedetail.model.CourseDetailService;
import com.fitanywhere.coursedetail.model.CourseDetailVO;
import com.fitanywhere.user.model.UserService;
import com.fitanywhere.user.model.UserVO;
import com.fitanywhere.user_course.model.UserCourseService;

@Controller
@RequestMapping("/course")
public class CourseController {

	@Autowired
	CourseService courseSvc;
	
	@Autowired
	AnnService annSvc;
	
	@Autowired
	UserService userSvc;
	
	@Autowired
	CoachService coachSvc;
	
	@Autowired
	CourseDetailService cdSvc;
	
	@Autowired
	UserCourseService ucSvc;

	@Autowired
	private VideoServiceImpl videoSvc;


	
	@GetMapping("test")
	public String test(ModelMap model) {
		return "front-end/course/test123";
	}



	@GetMapping("coach_course")
	public String coachCourse(ModelMap model) {
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		return "front-end/course/coach_course";
	}

	@GetMapping("create_course")
	public String addCourse(ModelMap model) {
		List<CourseVO> list = courseSvc.getAll();
		model.addAttribute("courseListData", list);
		return "front-end/course/create_course";
	}
	@GetMapping("update_course")
	public String updateCourse(ModelMap model,Integer crId) {
		CourseVO courseVO = courseSvc.getOneCourse(crId);
		model.addAttribute("courseVO", courseVO);
		return "front-end/course/update_course";
	}
	@GetMapping("create_course_video")
	public String addCourseVideo(ModelMap model) {
		return "front-end/course/create_course_video";
	}
	@GetMapping("coach_dashboard")
	public String dashboard(ModelMap model,HttpSession session) {
		Integer uId = 10001;//先寫死等登入
//		Integer uId = (Integer)session.getAttribute("uId");
		Integer coursecount = courseSvc.getCourseCount(uId);
		Integer studentcount = ucSvc.getStudentCount(uId);
		Integer starcount = courseSvc.getStarCount(uId);
		List<CourseVO> courseList = courseSvc.getAllCourseByuId(uId);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		model.addAttribute("coursecount", coursecount);
		model.addAttribute("courseList", courseList);
		model.addAttribute("studentcount", studentcount);
		model.addAttribute("starcount", starcount);
        return "front-end/course/coach_dashboard";
	}
	@GetMapping("course_announcement")
	public String announcement(ModelMap model,HttpSession session) {
		Integer uId = 10001;//先寫死等登入
//		Integer uId = (Integer)session.getAttribute("uId");
		List<AnnVO> announcementList = annSvc.getAll();
		
	    model.addAttribute("announcementList", announcementList);
		return "front-end/course/course_announcement";
	}
	@GetMapping("course_announce")
	public String announce(ModelMap model) {
		Integer uId = 10001;//先寫死等登入

		List<CourseVO> list2 = courseSvc.getAll();
		model.addAttribute("courseListData", list2);
		return "front-end/course/course_announce";
	}
	@GetMapping("coach_settings")
	public String settings(ModelMap model) {
		Integer uId = 10001 ;//先寫死等登入uId
		CoachVO coachVO = coachSvc.getOneCoach(uId);
		model.addAttribute("coachVO", coachVO);
		return "front-end/course/coach_settings";
	}
	
	/*
	 * This method will be called on addEmp.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("uploadVideo")
    public String handleFileUpload(@RequestParam("cdVideo") MultipartFile file, @RequestParam("crId") Integer crId, ModelMap model) {
		String cdVideo = null;
		Map<String, Integer> response = new HashMap<>();
		Integer uId = 10001;
//		Integer crId=1;
		CourseDetailVO coursedetailVO = new CourseDetailVO();
//		coursedetailVO.cousrsetCrId(crId);
		coursedetailVO.getCourseVO().setCrId(crId);
		// 在这里处理上传的文件
        if (!file.isEmpty()) {
            try {
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
	    		String dataStr = sdf.format(new Date());
	    		String userHome = System.getProperty("user.home");
	    		cdVideo = userHome+"/Desktop/cdVideo/"+dataStr+".mp4";
	    		coursedetailVO.setCdVideo(cdVideo);
	    		boolean isaddCDsuccess = cdSvc.addCourseDetail(coursedetailVO);
	    		if(isaddCDsuccess) {
	    			model.addAttribute("work", true);
	    		}
            	InputStream inputStream = file.getInputStream();
            	BufferedInputStream bis = new BufferedInputStream(inputStream);
            	FileOutputStream fos = new FileOutputStream(cdVideo);
            	BufferedOutputStream bos = new BufferedOutputStream(fos);
            	
                int bytesRead;
                while ((bytesRead = bis.read()) != -1) {
                	bos.write(bytesRead);
                }
                
                bos.close();
                fos.close();
                bis.close();
                inputStream.close();
            
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		Integer coursecount = courseSvc.getCourseCount(uId);
		List<CourseVO> courseList = courseSvc.getAllCourseByuId(uId);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		model.addAttribute("coursecount", coursecount);
		model.addAttribute("courseList", courseList);
        
        return "front-end/course/create_course_video";
    }
	@PostMapping("insert")
	public ResponseEntity<Map<String, Integer>> insertcourse(@Valid CourseVO courseVO,@RequestParam("crCoverfile") MultipartFile file , BindingResult result, ModelMap model) throws IOException {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		if( file != null) {
			courseVO.setCrCover(file.getBytes());
		}
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
//		result = removeFieldError(courseVO, result, "crId");

//		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的圖片時
//			model.addAttribute("errorMessage", "員工照片: 請上傳照片");
//		} else {
//			for (MultipartFile multipartFile : parts) {
//				byte[] buf = multipartFile.getBytes();
//				empVO.setUpFiles(buf);
//			}
//		}|| parts[0].isEmpty()
//		if (result.hasErrors() ) {
//			return "front-end/course/coach_dashboard";
//		}
//		String cdVideo = null;
//		
//		Part cdVideoPart = cdVideo.
//		InputStream inputStream = cdVideoPart.getInputStream();
//		byte[] videoArray = inputStream.readAllBytes();
//		
//		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
//		String dataStr = sdf.format(new Date());
//
//
//
		/*************************** 2.開始新增資料 *****************************************/
		long currentTimeMillis = System.currentTimeMillis();
		Timestamp currentTimestamp = new Timestamp(currentTimeMillis);
		courseVO.setCrCreateDate(currentTimestamp);
		courseVO.setCrEditDate(currentTimestamp);
		Integer crId = courseSvc.addCourse(courseVO);
		Map<String, Integer> response = new HashMap<>();
	    response.put("crId", crId);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		model.addAttribute("crId", crId);
		return ResponseEntity.ok(response); // 新增成功後重導至IndexController_inSpringBoot.java的第50行@GetMapping("/emp/listAllEmp")
	}

	/*
	 * This method will be called on listAllEmp.html form submission, handling POST request
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("crId") String crId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		// EmpService empSvc = new EmpService();
		CourseVO courseVO = courseSvc.getOneCourse(Integer.valueOf(crId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("courseVO", courseVO);
		return "back-end/course/update_course_input"; // 查詢完成後轉交update_emp_input.html
	}
	@PostMapping("course_announcement_update")
	public String getAnnoucement_For_Update(@RequestParam("anId") String anId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		AnnVO annVO = annSvc.getOneAnn(Integer.valueOf(anId));
		Integer crId = annVO.getCrId();
		CourseVO courseVO = courseSvc.getOneCourse(crId);
		String crTitle = courseVO.getCrTitle();
		Integer uId = 10001;//先寫死等登入

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("anId", anId);
		model.addAttribute("crTitle", crTitle);
		model.addAttribute("annVO", annVO);
		return "front-end/course/course_announcement_update"; // 查詢完成後轉交update_emp_input.html
	}

	/*
	 * This method will be called on update_emp_input.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("update")
	public String update(@Valid AnnVO annVO, BindingResult result, ModelMap model,@RequestParam("anDate") String anDate) throws IOException {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
//		result = removeFieldError(courseVO, result, "upFiles");

//		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的新圖片時
//			// EmpService empSvc = new EmpService();
//			byte[] upFiles = empSvc.getOneEmp(empVO.getEmpno()).getUpFiles();
//			empVO.setUpFiles(upFiles);
//		} else {
//			for (MultipartFile multipartFile : parts) {
//				byte[] upFiles = multipartFile.getBytes();
//				empVO.setUpFiles(upFiles);
//			}
//		}
//		if (result.hasErrors()) {
//			return "back-end/course/update_course_input";
//		}
		Integer uId = 10001;
		long currentTimeMillis = System.currentTimeMillis();
		Timestamp currentTimestamp = new Timestamp(currentTimeMillis);
		/*************************** 2.開始修改資料 *****************************************/
		annVO.setAnEditDate(currentTimestamp);
		annVO.setAnDate(java.sql.Timestamp.valueOf(anDate));
		
		annSvc.updateAnn(annVO);
		List<AnnVO> announcementList = annSvc.getAll();
	    model.addAttribute("announcementList", announcementList);
		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
//		courseVO = courseSvc.getOneCourse(Integer.valueOf(courseVO.getCrId()));
//		model.addAttribute("courseVO", courseVO);
		return "front-end/course/course_announcement"; // 修改成功後轉交listOneEmp.html
	}
	@PostMapping("updateUserProfile")
	public String updateUserProfile(@Valid UserVO userVO, BindingResult result, ModelMap model) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
//		result = removeFieldError(courseVO, result, "upFiles");

//		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的新圖片時
//			// EmpService empSvc = new EmpService();
//			byte[] upFiles = empSvc.getOneEmp(empVO.getEmpno()).getUpFiles();
//			empVO.setUpFiles(upFiles);
//		} else {
//			for (MultipartFile multipartFile : parts) {
//				byte[] upFiles = multipartFile.getBytes();
//				empVO.setUpFiles(upFiles);
//			}
//		}
//		if (result.hasErrors()) {
//			return "back-end/course/update_course_input";
//		}
		/*************************** 2.開始修改資料 *****************************************/
		boolean isprofileSuccess = userSvc.updateUserProfile(userVO);
		Integer uId = userVO.getuId();
		CoachVO coachVO = coachSvc.getOneCoach(uId);
		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
//		model.addAttribute("isprofileSuccess", userVO);
		model.addAttribute("coachVO", coachVO);
		model.addAttribute("isprofileSuccess",isprofileSuccess);
		return "front-end/course/coach_settings"; // 修改成功後轉交listOneEmp.html
	}
	@PostMapping("updatecoach")
	public String updatecoach(@Valid CoachVO coachVO, BindingResult result, ModelMap model) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
//		result = removeFieldError(courseVO, result, "upFiles");

//		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的新圖片時
//			// EmpService empSvc = new EmpService();
//			byte[] upFiles = empSvc.getOneEmp(empVO.getEmpno()).getUpFiles();
//			empVO.setUpFiles(upFiles);
//		} else {
//			for (MultipartFile multipartFile : parts) {
//				byte[] upFiles = multipartFile.getBytes();
//				empVO.setUpFiles(upFiles);
//			}
//		}
//		if (result.hasErrors()) {
//			return "back-end/course/update_course_input";
//		}
		/*************************** 2.開始修改資料 *****************************************/
		boolean iscoachSuccess = coachSvc.updateCoach(coachVO);
		Integer uId = coachVO.getuId();
		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("iscoachSuccess", iscoachSuccess);
		model.addAttribute("coachVO", coachVO);
		model.addAttribute("success", "- (修改成功)");
		return "front-end/course/coach_settings"; // 修改成功後轉交listOneEmp.html
	}
	@PostMapping("updatecourse")
	public String updatecourse(@Valid CourseVO courseVO, BindingResult result, ModelMap model,@RequestParam("crCreateDate") String crCreateDate) throws IOException {
		
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
//		result = removeFieldError(courseVO, result, "upFiles");
		
//		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的新圖片時
//			// EmpService empSvc = new EmpService();
//			byte[] upFiles = empSvc.getOneEmp(empVO.getEmpno()).getUpFiles();
//			empVO.setUpFiles(upFiles);
//		} else {
//			for (MultipartFile multipartFile : parts) {
//				byte[] upFiles = multipartFile.getBytes();
//				empVO.setUpFiles(upFiles);
//			}
//		}
//		if (result.hasErrors()) {
//			return "back-end/course/update_course_input";
//		}
		/*************************** 2.開始修改資料 *****************************************/
		courseVO.setCrCreateDate(java.sql.Timestamp.valueOf(crCreateDate));
		long currentTimeMillis = System.currentTimeMillis();
		Timestamp currentTimestamp = new Timestamp(currentTimeMillis);
		courseVO.setCrEditDate(currentTimestamp);
		courseSvc.updateCourse(courseVO);
//		Integer uId = coachVO.getuId();
		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
//		model.addAttribute("iscoachSuccess", iscoachSuccess);
//		model.addAttribute("coachVO", coachVO);
		model.addAttribute("success", "- (修改成功)");
		return "front-end/course/coach_course"; // 修改成功後轉交listOneEmp.html
	}

	/*
	 * This method will be called on listAllEmp.html form submission, handling POST request
	 */
	@PostMapping("delete")
	public String delete(@RequestParam("crId") String crId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始刪除資料 *****************************************/
		// EmpService empSvc = new EmpService();
		courseSvc.deleteCourse(Integer.valueOf(crId));
		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
		List<CourseVO> list = courseSvc.getAll();
		model.addAttribute("courseListData", list);
		model.addAttribute("success", "- (刪除成功)");
		return "back-end/course/listAllCourse"; // 刪除完成後轉交listAllEmp.html
	}
	@PostMapping("update_password")
    public String updateUserPassword(ModelMap model , @RequestParam Integer uId, @RequestParam String oldPassword, @RequestParam String newPassword) {
		Map<String, String> resultMap = new HashMap<>();
		CoachVO coachVO = coachSvc.getOneCoach(uId);
		boolean ispasswordSuccess = userSvc.updateUserPassword(uId, oldPassword, newPassword);
		if(ispasswordSuccess) {
			model.addAttribute("success", true);
		} else {
			model.addAttribute("fail", true);
		}
		model.addAttribute("coachVO", coachVO);
		
		return "front-end/course/coach_settings";
    }
	
	// 可再自行添加需要的查詢PathVariable
		@GetMapping(value = "cdVideo")
		public ResponseEntity<StreamingResponseBody> sendStreamingVideo(String videoId,
				@RequestHeader(value = "Range", required = false) String rangeHeader) {
			videoId = "/Users/andy/Desktop/cdVideo/2024-03-08-12:45:07.mp4";
			ResponseEntity<StreamingResponseBody> response = null;
			try {
				response = videoSvc.getPartialVideo(rangeHeader, videoId);
				
				
			} catch (IOException ie) {
				ie.printStackTrace();
			}
			return response;
		}

	/*
	 * 第一種作法 Method used to populate the List Data in view. 如 : 
	 * <form:select path="deptno" id="deptno" items="${deptListData}" itemValue="deptno" itemLabel="dname" />
	 */
//	@ModelAttribute("deptListData")
//	protected List<DeptVO> referenceListData() {
//		// DeptService deptSvc = new DeptService();
//		List<DeptVO> list = deptSvc.getAll();
//		return list;
//	}

	/*
	 * 【 第二種作法 】 Method used to populate the Map Data in view. 如 : 
	 * <form:select path="deptno" id="deptno" items="${depMapData}" />
	 */
//	@ModelAttribute("deptMapData") //
//	protected Map<Integer, String> referenceMapData() {
//		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
//		map.put(10, "財務部");
//		map.put(20, "研發部");
//		map.put(30, "業務部");
//		map.put(40, "生管部");
//		return map;
//	}

	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(CourseVO courseVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(courseVO, "courseVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
	/*=================取值========================*/
	@ModelAttribute("uName")
	public String getuName(Integer uId) {
		uId = 10001 ;
		String uName = userSvc.getUser(uId).getuName();
		return uName;
	}
	
	@ModelAttribute("userVO")
	public UserVO getUser(Integer uId) {
		uId = 10001 ;
		UserVO userVO = userSvc.getUser(uId);
		return userVO;
	}
	
	@ModelAttribute("uHeadshot")
	public String getuHeadshot(Integer uId) {
		uId = 10001 ;
		byte [] uHeadshot = userSvc.getUserHeadshot(uId);
		return Base64.getEncoder().encodeToString(uHeadshot);
	}
	
	@ModelAttribute("course0")
	public List<CourseStatus0DTO> getcourse0(Integer uId) {
		uId = 10001 ;
		List<CourseStatus0DTO> CourseStatus0DTO = courseSvc.getCourseByStatus0(uId);
		return CourseStatus0DTO;
	}
	
	@ModelAttribute("course1")
	public List<CourseStatus1DTO> getcourse1(Integer uId) {
		uId = 10001 ;
		List<CourseStatus1DTO> CourseStatus1DTO = courseSvc.getCourseByStatus1(uId);
		return CourseStatus1DTO;
	}
	
	@ModelAttribute("course2")
	public List<CourseStatus2DTO> getcourse2(Integer uId) {
		uId = 10001 ;
		List<CourseStatus2DTO> CourseStatus2DTO = courseSvc.getCourseByStatus2(uId);
		return CourseStatus2DTO;
	}
	
	@ModelAttribute()
	public String getVideo(Integer crId) {
		
		return "";
	}

}