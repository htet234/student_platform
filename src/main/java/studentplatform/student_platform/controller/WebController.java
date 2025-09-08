package studentplatform.student_platform.controller;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.beans.PropertyEditorSupport;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.format.TextStyle;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.time.Month;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import studentplatform.student_platform.model.Reward;
import studentplatform.student_platform.model.RewardExchange;
import studentplatform.student_platform.model.Staff;
import studentplatform.student_platform.model.Student;

import studentplatform.student_platform.service.AdminService;
import studentplatform.student_platform.service.RewardService;
import studentplatform.student_platform.service.RewardExchangeService;
import studentplatform.student_platform.service.StaffService;
import studentplatform.student_platform.service.StudentService;
import studentplatform.student_platform.util.DateTimeUtil;
import studentplatform.student_platform.model.Admin;
import studentplatform.student_platform.model.Attendance;
import studentplatform.student_platform.model.Club;
import studentplatform.student_platform.model.ClubMembership;
import studentplatform.student_platform.model.Event;
import studentplatform.student_platform.model.Activity;
import studentplatform.student_platform.model.ActivityParticipation;
import studentplatform.student_platform.model.EventParticipation;


import studentplatform.student_platform.service.AttendanceService;
import studentplatform.student_platform.model.Semester;

import studentplatform.student_platform.model.SemesterGrade;
import studentplatform.student_platform.service.SemesterService;
import studentplatform.student_platform.service.SemesterGradeService;
import studentplatform.student_platform.service.ClubService;
import studentplatform.student_platform.service.ActivityParticipationService;
import studentplatform.student_platform.service.EventService;

import studentplatform.student_platform.service.ActivityService;
import studentplatform.student_platform.model.SpinWheel;
import studentplatform.student_platform.model.SpinWheelHistory;
import studentplatform.student_platform.model.SpinWheelItem;
import studentplatform.student_platform.service.SpinWheelService;
import studentplatform.student_platform.service.SpinWheelItemService;
import studentplatform.student_platform.service.SpinWheelHistoryService;
import studentplatform.student_platform.repository.SpinWheelHistoryRepository;
import studentplatform.student_platform.repository.EventParticipationRepository;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;





@Controller
public class WebController {

    private final StudentService studentService;
    private final StaffService staffService;
    private final RewardService rewardService;
    private final RewardExchangeService rewardExchangeService;
    private final AdminService adminService;

    private final EventService eventService;
    private final ClubService clubService;
    private final ActivityService activityService;
    private final ActivityParticipationService activityParticipationService;

    private final AttendanceService attendanceService;
    private final SemesterService semesterService;
    private final SemesterGradeService semesterGradeService;
// Spinwheel services
    private final SpinWheelService spinWheelService;
    private final SpinWheelItemService spinWheelItemService;
    private final SpinWheelHistoryService spinWheelHistoryService;
    private final EventParticipationRepository eventParticipationRepository;
    private final SpinWheelHistoryRepository spinWheelHistoryRepository;
    
    @Autowired
    public WebController(StudentService studentService, StaffService staffService, 
                         RewardService rewardService, RewardExchangeService rewardExchangeService,
                         AdminService adminService,
                         EventService eventService, ClubService clubService, ActivityService activityService,
                         ActivityParticipationService activityParticipationService,
                         AttendanceService attendanceService,
                         SemesterService semesterService,
                         SemesterGradeService semesterGradeService,
                         SpinWheelService spinWheelService, SpinWheelItemService spinWheelItemService,
                         SpinWheelHistoryService spinWheelHistoryService, SpinWheelHistoryRepository spinWheelHistoryRepository,
                         EventParticipationRepository eventParticipationRepository) {

        this.studentService = studentService;
        this.staffService = staffService;
        this.rewardService = rewardService;
        this.rewardExchangeService = rewardExchangeService;
        this.adminService = adminService;
        this.eventService = eventService;

        this.clubService = clubService;
        this.activityService = activityService;
        this.activityParticipationService = activityParticipationService;

        this.attendanceService = attendanceService;
        this.semesterService = semesterService;
        this.semesterGradeService = semesterGradeService;
         this.spinWheelService = spinWheelService;
        this.spinWheelItemService = spinWheelItemService;
        this.spinWheelHistoryService = spinWheelHistoryService;
        this.spinWheelHistoryRepository = spinWheelHistoryRepository;
        this.eventParticipationRepository = eventParticipationRepository;
    }
    
    // Update the home method
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        // Check if user is logged in
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        
        // Add counts for dashboard statistics
        model.addAttribute("studentCount", studentService.getAllStudents().size());
        model.addAttribute("staffCount", staffService.getAllStaff().size());
        model.addAttribute("rewardCount", rewardService.getAllRewards().size());
        model.addAttribute("eventCount", eventService.getAllEvents().size());
   
        return "login";
    }
    
    // Student Management
    @GetMapping("/students")
    public String students(Model model, @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("students", studentService.searchStudentsByName(keyword));
        } else {
            model.addAttribute("students", studentService.getAllStudents());
        }
        return "students/list";
    }
    
    @GetMapping("/students/create")
    public String createStudentForm(Model model) {
        Student student = new Student();
        student.initializeNewStudent(); // Initialize default values
        model.addAttribute("student", student);
        return "students/form";
    }
    
    @GetMapping("/students/edit/{id}")
    public String editStudentForm(@PathVariable Long id, Model model) {
        return studentService.getStudentById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    return "students/form";
                })
                .orElse("redirect:/students");
    }
    
    @PostMapping("/students/save")
    public String saveStudent(@Valid @ModelAttribute("student") Student student, BindingResult result) {
        if (result.hasErrors()) {
            return "students/form";
        }
        studentService.saveStudent(student);
        return "redirect:/students";
    }
    
    @GetMapping("/students/view/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {
        return studentService.getStudentById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    return "students/view";
                })
                .orElse("redirect:/students");
    }
    
    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        studentService.deleteStudent(id);
        return "redirect:/students";
    }
    
    // Staff Management
    @GetMapping("/staff")
    public String staff(Model model, @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("staff", staffService.searchStaffByName(keyword));
        } else {
            model.addAttribute("staff", staffService.getAllStaff());
        }
        return "staff/list";
    }
    
    @GetMapping("/staff/create")
    public String createStaffForm(Model model) {
        model.addAttribute("staff", new Staff());
        return "staff/form";
    }
    
    @GetMapping("/staff/edit/{id}")
    public String editStaffForm(@PathVariable Long id, Model model) {
        return staffService.getStaffById(id)
                .map(staff -> {
                    model.addAttribute("staff", staff);
                    return "staff/form";
                })
                .orElse("redirect:/staff");
    }
    
    @PostMapping("/staff/save")
    public String saveStaff(@Valid @ModelAttribute("staff") Staff staff, BindingResult result) {
        if (result.hasErrors()) {
            return "staff/form";
        }
        staffService.saveStaff(staff);
        return "redirect:/staff";
    }
    
    @GetMapping("/staff/view/{id}")
    public String viewStaff(@PathVariable Long id, Model model) {
        return staffService.getStaffById(id)
                .map(staff -> {
                    model.addAttribute("staff", staff);
                    return "staff/view";
                })
                .orElse("redirect:/staff");
    }
    
    @GetMapping("/staff/delete/{id}")
    public String deleteStaff(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        staffService.deleteStaff(id);
        return "redirect:/staff";
    }
    
    // Rewards Management
    @GetMapping("/rewards")
    public String rewards(Model model, @RequestParam(required = false) String keyword, 
                         @RequestParam(required = false) Boolean showInactive) {
        List<Reward> rewardsList;
        
        if (keyword != null && !keyword.isEmpty()) {
            rewardsList = rewardService.searchRewardsByKeyword(keyword);
        } else if (showInactive != null && showInactive) {
            rewardsList = rewardService.getAllRewards();
        } else {
            rewardsList = rewardService.getActiveRewards();
        }
        
        model.addAttribute("rewards", rewardsList);
        model.addAttribute("showInactive", showInactive != null && showInactive);
        return "rewards/list";
    }
    
    @GetMapping("/rewards/create")
    public String createRewardForm(Model model) {
        model.addAttribute("reward", new Reward());
        model.addAttribute("staffList", staffService.getAllStaff());
        return "rewards/form";
    }
    
    @GetMapping("/rewards/edit/{id}")
    public String editRewardForm(@PathVariable Long id, Model model) {
        return rewardService.getRewardById(id)
                .map(reward -> {
                    model.addAttribute("reward", reward);
                    model.addAttribute("staffList", staffService.getAllStaff());
                    return "rewards/form";
                })
                .orElse("redirect:/rewards");
    }
    
    @PostMapping("/rewards/save")
    public String saveReward(@Valid @ModelAttribute("reward") Reward reward, BindingResult result) {
        if (result.hasErrors()) {
            return "rewards/form";
        }
        rewardService.saveReward(reward);
        return "redirect:/rewards";
    }
    
    @GetMapping("/rewards/view/{id}")
    public String viewReward(@PathVariable Long id, Model model) {
        return rewardService.getRewardById(id)
                .map(reward -> {
                    model.addAttribute("reward", reward);
                    return "rewards/view";
                })
                .orElse("redirect:/rewards");
    }
    
    @GetMapping("/rewards/delete/{id}")
    public String deleteReward(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            rewardService.deleteReward(id);
            redirectAttributes.addFlashAttribute("success", "Reward deleted successfully.");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rewards";
    }
    
    @GetMapping("/rewards/deactivate/{id}")
    public String deactivateReward(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        rewardService.deactivateReward(id);
        redirectAttributes.addFlashAttribute("success", "Reward deactivated successfully.");
        return "redirect:/rewards";
    }
    
    @GetMapping("/rewards/activate/{id}")
    public String activateReward(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        rewardService.activateReward(id);
        redirectAttributes.addFlashAttribute("success", "Reward activated successfully.");
        return "redirect:/rewards";
    }
    
    // Points Management
   
    
    
    // Admin Dashboard
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        // Add counts for dashboard statistics
        model.addAttribute("studentCount", studentService.getAllStudents().size());
        model.addAttribute("staffCount", staffService.getAllStaff().size());
        model.addAttribute("rewardCount", rewardService.getAllRewards().size());
                model.addAttribute("spinWheelCount", spinWheelService.getAllSpinWheels().size());
        // Add pending registrations count
        model.addAttribute("pendingStudentCount", studentService.findByStatus(Student.AccountStatus.PENDING).size());
        model.addAttribute("pendingStaffCount", staffService.findByStatus(Staff.AccountStatus.PENDING).size());
        
        return "admin/dashboard";
    }
    
    // Pending Student Registrations
    @GetMapping("/admin/pending-students")
    public String pendingStudents(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("pendingStudents", studentService.findByStatus(Student.AccountStatus.PENDING));
        return "admin/pending-students";
    }
    
    // Approve Student Registration
    @PostMapping("/admin/approve-student/{id}")
    public String approveStudentRegistration(@PathVariable Long id, @RequestParam String studentId, RedirectAttributes redirectAttributes) {
        // Validate student ID format
        if (!studentId.matches("TNT-\\d{4}")) {
            redirectAttributes.addFlashAttribute("error", "Invalid Student ID format. Must be TNT-**** (e.g., TNT-1234)");
            return "redirect:/admin/pending-students";
        }
        
        try {
            // Update student ID and approve registration
            studentService.updateStudentIdAndApprove(id, studentId);
            redirectAttributes.addFlashAttribute("success", "Student approved successfully with ID: " + studentId);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error approving student: " + e.getMessage());
        }
        
        return "redirect:/admin/pending-students";
    }
    
    // Reject Student Registration
    @PostMapping("/admin/reject-student/{id}")
    public String rejectStudentRegistration(@RequestParam String studentId) {  
        studentService.rejectRegistration(studentId);
        return "redirect:/admin/pending-students";
    }
    
    // Pending Staff Registrations
    @GetMapping("/admin/pending-staff")
    public String pendingStaff(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("pendingStaff", staffService.findByStatus(Staff.AccountStatus.PENDING));
        return "admin/pending-staff";
    }
    
    // Approve Staff Registration
    @PostMapping("/admin/approve-staff/{id}")
    public String approveStaffRegistration(@PathVariable Long id) {
        staffService.approveRegistration(id);
        return "redirect:/admin/pending-staff";
    }
    
    // Reject Staff Registration
    @PostMapping("/admin/reject-staff/{id}")
    public String rejectStaffRegistration(@PathVariable Long id) {
        staffService.rejectStaff(id);
        return "redirect:/admin/pending-staff";
    }
    
    // Pending Account Page
    @GetMapping("/pending")
    public String pendingAccount() {
        return "pending";
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        return "register";
    }
    
    @PostMapping("/register")
    public String processRegistration(@RequestParam String username,
                                     @RequestParam String email,
                                     @RequestParam String password,
                                     @RequestParam String confirmPassword,
                                     @RequestParam String role,
                                     Model model) {
        
        
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register";
        }
        
        if (role.equals("STUDENT")) {
            if (studentService.findByUsername(username).isPresent()) {
                model.addAttribute("error", "Username already exists");
                return "register";
            }
            
            Student student = new Student();
            student.setUsername(username);
            String Stuhashpasword=studentService.hashPassword(password);
            student.setPassword(Stuhashpasword); 
            student.setEmail(email);
            student.setStatus(Student.AccountStatus.PENDING);
            
           
            student.setFirstName("Pending");
            student.setLastName("Pending");
            student.setDepartment("Pending");
            student.setYear(1);
            
            studentService.saveStudent(student);
            
        } else if (role.equals("STAFF")) {
            if (staffService.findByUsername(username).isPresent()) {
                model.addAttribute("error", "Username already exists");
                return "register";
            }
            
            // Create new staff with pending status
            Staff staff = new Staff();
            staff.setUsername(username);
            String Staffhashpasword=studentService.hashPassword(password);
            staff.setPassword(Staffhashpasword); 
            staff.setEmail(email);
            staff.setStatus(Staff.AccountStatus.PENDING);
            
            // Set default values for required fields
            staff.setFirstName("Pending");
            staff.setLastName("Approval");
            staff.setDepartment("Pending");
            staff.setPosition("Pending");
            
            staffService.saveStaff(staff);
        } else {
            model.addAttribute("error", "Invalid role selected");
            return "register";
        }
        
        model.addAttribute("message", "Registration successful! Your account is pending approval by an administrator.");
        return "login";
    }
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }
    
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, 
                              @RequestParam String password,
                              @RequestParam String role,
                              HttpSession session,
                              Model model) {
        
        if(role.equals("STUDENT")) {
            Optional<Student> studentOpt = studentService.findByUsername(username);
            if(studentOpt.isPresent()) {
                Student student = studentOpt.get();
                
                // Check if account is pending or rejected
                if (student.getStatus() == Student.AccountStatus.PENDING) {
                    model.addAttribute("message", "Your account is pending approval.");
                    return "pending";
                } else if (student.getStatus() == Student.AccountStatus.REJECTED) {
                    model.addAttribute("error", "Your registration has been rejected.");
                    return "login";
                }
                
                // Continue with authentication
                if(studentService.authStudent(username, password)) {
                    session.setAttribute("user", student);
                    session.setAttribute("userId", student.getId());
                    session.setAttribute("userRole", "STUDENT");
                    return "redirect:/students/dashboard/" + student.getId();
                }
            }
        } else if(role.equals("STAFF")) {
            Optional<Staff> staffOpt = staffService.findByUsername(username);
            if(staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                
                // Check if account is pending or rejected
                if (staff.getStatus() == Staff.AccountStatus.PENDING) {
                    model.addAttribute("message", "Your account is pending approval.");
                    return "pending";
                } else if (staff.getStatus() == Staff.AccountStatus.REJECTED) {
                    model.addAttribute("error", "Your registration has been rejected.");
                    return "login";
                }
                
                // Continue with authentication
                if(staffService.authStaff(username, password)) {
                    Staff staffWithRewards = staffService.getStaffWithRewards(username);
                    session.setAttribute("user", staffWithRewards);
                    session.setAttribute("userId", staffWithRewards.getId());
                    session.setAttribute("userRole", "STAFF");
                    return "redirect:/staff/dashboard";
                }
            }
        } else if(role.equals("ADMIN")) {
            // Admin authentication (assuming you have an AdminService)
            if(adminService.authAdmin(username, password)) {
                Optional<Admin> adminOpt = adminService.findByUsername(username);
                if(adminOpt.isPresent()) {
                    Admin admin = adminOpt.get();
                    session.setAttribute("user", admin);
                    session.setAttribute("userId", admin.getId());
                    session.setAttribute("userRole", "ADMIN");
                    return "redirect:/admin/dashboard";
                }
            }
        }
        
        model.addAttribute("error", "Invalid username, password, or role");
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");
        return "redirect:/login";
    }
    


    @GetMapping("/admin/clubs")
    public String adminClubs(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("clubs", clubService.getAllClubs());
        // Expose service so JSP can compute active member counts per club
        model.addAttribute("clubService", clubService);
        return "admin/clubs";
    }
    
    @GetMapping("/admin/clubmanagement")
    public String adminClubManagement(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("clubs", clubService.getAllClubs());
        return "admin/clubmanagement";
    }
    
    @PostMapping("/admin/clubs/create")
    public String createClub(@Valid @ModelAttribute("club") Club club, BindingResult result, 
                           HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check the form data");
            return "redirect:/admin/clubmanagement";
        }
        
        try {
            clubService.createClub(club, admin);
            redirectAttributes.addFlashAttribute("success", "Club created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create club: " + e.getMessage());
        }
        
        return "redirect:/admin/clubmanagement";
    }
    
    @GetMapping("/admin/clubs/edit/{id}")
    public String editClubForm(@PathVariable Long id, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        return clubService.getClubById(id)
                .map(club -> {
                    model.addAttribute("club", club);
                    return "admin/club-edit";
                })
                .orElse("redirect:/admin/clubmanagement");
    }
    
    @PostMapping("/admin/clubs/update/{id}")
    public String updateClub(@PathVariable Long id, @Valid @ModelAttribute("club") Club club, 
                           BindingResult result, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check the form data");
            return "redirect:/admin/clubs/edit/" + id;
        }
        
        try {
            club.setId(id);
            club.setCreatedBy(admin);
            clubService.updateClub(club);
            redirectAttributes.addFlashAttribute("success", "Club updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update club: " + e.getMessage());
        }
        
        return "redirect:/admin/clubmanagement";
    }
    
    @PostMapping("/admin/clubs/delete/{id}")
    public String deleteClub(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("=== DELETE CLUB REQUEST ===");
        System.out.println("Club ID: " + id);
        
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            System.out.println("No admin in session, redirecting to login");
            return "redirect:/login";
        }
        
        System.out.println("Admin: " + admin.getUsername() + " (ID: " + admin.getId() + ")");
        
        try {
            System.out.println("Attempting to delete club...");
            clubService.deleteClub(id);
            System.out.println("Club deleted successfully");
            redirectAttributes.addFlashAttribute("success", "Club deleted successfully!");
        } catch (Exception e) {
            System.err.println("Error deleting club: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to delete club: " + e.getMessage());
        }
        
        return "redirect:/admin/clubmanagement";
    }

    @GetMapping("/admin/activitymanagement")
    public String adminActivityManagement(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("clubs", clubService.getAllClubs());
        model.addAttribute("activities", activityService.getAllActivities());
        return "admin/activitymanagement";
    }

    @GetMapping("/admin/activities/create")
    public String createActivityForm(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("activity", new Activity());
        model.addAttribute("clubs", clubService.getAllClubs());
        return "admin/activity-form";
    }

    @GetMapping("/admin/activities/edit/{id}")
    public String editActivityForm(@PathVariable Long id, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        return activityService.getActivityById(id)
                .map(activity -> {
                    model.addAttribute("activity", activity);
                    model.addAttribute("clubs", clubService.getAllClubs());
                    return "admin/activity-form";
                })
                .orElse("redirect:/admin/activitymanagement");
    }

    @PostMapping("/admin/activities/create")
    public String createActivity(@Valid @ModelAttribute("activity") Activity activity, BindingResult result, 
                               HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check the form data");
            return "redirect:/admin/activities/create";
        }
        
        try {
            // Set the club if clubId is provided
            if (activity.getClub() != null && activity.getClub().getId() != null) {
                Optional<Club> club = clubService.getClubById(activity.getClub().getId());
                if (club.isPresent()) {
                    activity.setClub(club.get());
                }
            }
            
            activityService.createActivity(activity, admin);
            redirectAttributes.addFlashAttribute("success", "Activity created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create activity: " + e.getMessage());
        }
        
        return "redirect:/admin/activitymanagement";
    }

    @PostMapping("/admin/activities/update")
    public String updateActivity(@Valid @ModelAttribute("activity") Activity activity, BindingResult result, 
                               HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please check the form data");
            return "redirect:/admin/activities/edit/" + activity.getId();
        }
        
        try {
            // Set the club if clubId is provided
            if (activity.getClub() != null && activity.getClub().getId() != null) {
                Optional<Club> club = clubService.getClubById(activity.getClub().getId());
                if (club.isPresent()) {
                    activity.setClub(club.get());
                }
            }
            
            activityService.updateActivity(activity);
            redirectAttributes.addFlashAttribute("success", "Activity updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update activity: " + e.getMessage());
        }
        
        return "redirect:/admin/activitymanagement";
    }

    @GetMapping("/admin/activities/delete/{id}")
    public String deleteActivity(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            activityService.deleteActivity(id);
            redirectAttributes.addFlashAttribute("success", "Activity deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete activity: " + e.getMessage());
        }
        
        return "redirect:/admin/activitymanagement";
    }

    @GetMapping("/admin/studentmonitoring")
    public String adminStudentMonitoring(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        // Clubs list
        List<Club> clubs = clubService.getAllClubs();
        model.addAttribute("clubs", clubs);

        // Collect all active memberships across clubs
        List<ClubMembership> activeMemberships = new ArrayList<>();
        for (Club club : clubs) {
            try {
                activeMemberships.addAll(clubService.getActiveMembershipsByClub(club));
            } catch (Exception e) {
                System.err.println("Error loading active memberships for club " + club.getId() + ": " + e.getMessage());
            }
        }
        model.addAttribute("activeMemberships", activeMemberships);

        // Pre-compute activity participation counts per student
        Map<Long, Integer> activityCounts = new HashMap<>();
        for (ClubMembership membership : activeMemberships) {
            try {
                Student mStudent = membership.getStudent();
                if (mStudent != null) {
                    int count = activityParticipationService.getParticipationsByStudent(mStudent).size();
                    activityCounts.put(mStudent.getId(), count);
                }
            } catch (Exception e) {
                // continue on errors
            }
        }
        model.addAttribute("activityCounts", activityCounts);
        return "admin/studentmonitoring";
    }

    @GetMapping("/admin/test-links")
    public String testLinks(Model model) {
        return "admin/test-links";
    }
    
    
            
          @GetMapping("/admin/events")
    public String adminEvents(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        // Add all events to the model instead of separating them
        model.addAttribute("events", eventService.getAllEvents());
        
        return "admin/events";
    }
    
    @GetMapping("/admin/events/create")
    public String createEventForm(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("event", new Event());
        return "admin/event-form";
    }
    
    @PostMapping("/admin/events/save")
    public String saveEvent(@ModelAttribute("event") Event event,
                            BindingResult result,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            jakarta.servlet.http.HttpServletRequest request,
                            org.springframework.ui.Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }

        // Reconstruct start/end from separate inputs if not bound
        try {
            if (event.getStartTime() == null || event.getEndTime() == null) {
                String date = request.getParameter("eventDate");
                String startOnly = request.getParameter("startTimeOnly");
                String endOnly = request.getParameter("endTimeOnly");
                if (date != null && !date.isBlank() && startOnly != null && !startOnly.isBlank() && endOnly != null && !endOnly.isBlank()) {
                    LocalDate d = LocalDate.parse(date);
                    LocalTime st = LocalTime.parse(startOnly.length() > 5 ? startOnly.substring(0,5) : startOnly);
                    LocalTime et = LocalTime.parse(endOnly.length() > 5 ? endOnly.substring(0,5) : endOnly);
                    event.setStartTime(LocalDateTime.of(d, st));
                    event.setEndTime(LocalDateTime.of(d, et));
                }
            }
        } catch (Exception parseEx) {
            result.rejectValue("startTime", "ParseError", "Invalid date/time format");
        }

        // Minimal validations (keep end-after-start only)
        if (event.getStartTime() == null) {
            result.rejectValue("startTime", "NotNull", "Start time is required");
        }
        if (event.getEndTime() == null) {
            result.rejectValue("endTime", "NotNull", "End time is required");
        }
        if (event.getStartTime() != null && event.getEndTime() != null) {
            if (!event.getEndTime().isAfter(event.getStartTime())) {
                result.rejectValue("endTime", "EndBeforeStart", "End time must be after start time");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("event", event);
            return "admin/event-form";
        }

        try {
            // Ensure creator is set from session (binding may not populate nested object reliably)
            event.setCreatedBy(admin);
            eventService.saveEvent(event);
            redirectAttributes.addFlashAttribute("success", "Event saved successfully!");
            return "redirect:/admin/events";
        } catch (Exception e) {
            System.err.println("Error saving event: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("event", event);
            redirectAttributes.addFlashAttribute("error", "Error saving event: " + e.getMessage());
            return "admin/event-form";
        }
    }
    
    @GetMapping("/admin/events/edit/{id}")
    public String editEventForm(@PathVariable Long id, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        return eventService.getEventById(id)
                .map(event -> {
                    model.addAttribute("event", event);
                    return "admin/event-form";
                })
                .orElse("redirect:/admin/events");
    }
    
    @GetMapping("/admin/events/view/{id}")
    public String viewEvent(@PathVariable Long id, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        return eventService.getEventById(id)
                .map(event -> {
                    model.addAttribute("event", event);
                    model.addAttribute("pendingParticipations", eventService.getPendingParticipationsByEvent(event));
                    model.addAttribute("approvedParticipations", eventService.approveParticipationsByEvent(event));
                    return "admin/event-view";
                })
                .orElse("redirect:/admin/events");
    }
    
    @GetMapping("/admin/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        eventService.deleteEvent(id);
        return "redirect:/admin/events";
    }
    
    
  
    
    @PostMapping("/admin/reject-club-participation/{id}")
    public String rejectClubParticipation(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        // TODO: Implement when ClubService is available
        // clubService.rejectParticipation(id, admin);
        return "redirect:/admin/club-participations";
    }
    
    @PostMapping("/admin/approve-event-participation/{id}")
    public String approveEventParticipation(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            // First approve the participation
            EventParticipation participation = eventService.approveParticipation(id, admin);
            
            // Then immediately award points without waiting for event end time
            eventService.awardPointsForParticipation(participation);
            
            redirectAttributes.addFlashAttribute("success", "Participation approved and points awarded to student.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/admin/event-participations";
    }
    
    @PostMapping("/admin/reject-event-participation/{id}")
    public String rejectEventParticipation(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        eventService.rejectParticipation(id, admin);
        return "redirect:/admin/event-participations";
    }
    // Activity participations moderation (Admin)
    @GetMapping("/admin/activity-participations")
    public String adminActivityParticipations(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        try {
            List<ActivityParticipation> pending = activityParticipationService.getParticipationsByStatus(ActivityParticipation.ParticipationStatus.PENDING);
            List<ActivityParticipation> approved = activityParticipationService.getParticipationsByStatus(ActivityParticipation.ParticipationStatus.APPROVED);
            model.addAttribute("pendingParticipations", pending);
            model.addAttribute("approvedParticipations", approved);
        } catch (Exception e) {
            model.addAttribute("pendingParticipations", new ArrayList<>());
            model.addAttribute("approvedParticipations", new ArrayList<>());
        }
        return "admin/activity-participations";
    }

    // Event participations moderation (Admin)
    @GetMapping("/admin/event-participations")
    public String adminEventParticipations(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        try {
            List<EventParticipation> pending = eventService.getParticipationsByStatus(EventParticipation.ParticipationStatus.PENDING);
            List<EventParticipation> approved = eventService.getParticipationsByStatus(EventParticipation.ParticipationStatus.APPROVED);
            model.addAttribute("pendingParticipations", pending);
            model.addAttribute("approvedParticipations", approved);
        } catch (Exception e) {
            model.addAttribute("pendingParticipations", new ArrayList<>());
            model.addAttribute("approvedParticipations", new ArrayList<>());
        }
        return "admin/event-participation";
    }

    // Manual trigger for testing point awarding (Admin only)
    @PostMapping("/admin/trigger-event-points")
    public String triggerEventPoints(HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        try {
            eventService.manuallyAwardPointsForEndedEvents();
            redirectAttributes.addFlashAttribute("success", "Event points awarding triggered successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error triggering point awarding: " + e.getMessage());
        }
        return "redirect:/admin/event-participations";
    }

    @PostMapping("/admin/approve-activity-participation/{id}")
    public String approveActivityParticipation(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        try {
            activityParticipationService.approveParticipation(id, admin);
            redirectAttributes.addFlashAttribute("success", "Participation approved and points awarded.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/activity-participations";
    }

    @PostMapping("/admin/reject-activity-participation/{id}")
    public String rejectActivityParticipation(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        try {
            activityParticipationService.rejectParticipation(id, admin);
            redirectAttributes.addFlashAttribute("success", "Participation rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/activity-participations";
    }


    // Student Club and Event Interface
    @GetMapping("/clubs")
    public String studentClubs(Model model) {
        model.addAttribute("clubs", clubService.getAllClubs());
        return "clubs/list";
    }
    
    @GetMapping("/students/clubs")
    public String studentClubsView(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        List<Club> clubs = clubService.getAllClubs();
        model.addAttribute("clubs", clubs);
        model.addAttribute("student", student);
        model.addAttribute("clubService", clubService);

        // Build membership status map for UI and determine if student reached membership limit
        Map<Long, Boolean> membershipStatus = new HashMap<>();
        int activeMembershipCount = clubService.getActiveMembershipsByStudent(student).size();
        boolean hasReachedLimit = activeMembershipCount >= 2;
        for (Club club : clubs) {
            boolean isMember = clubService.isStudentMemberOfClub(student, club);
            membershipStatus.put(club.getId(), isMember);
        }
        model.addAttribute("membershipStatus", membershipStatus);
        model.addAttribute("hasReachedLimit", hasReachedLimit);
        return "students/clubview";
    }
    
    @GetMapping("/students/debug")
    public String debugPage(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        // Test database connection
        try {
            List<Club> clubs = clubService.getAllClubs();
            System.out.println("Found " + clubs.size() + " clubs in database");
            model.addAttribute("clubs", clubs);
        } catch (Exception e) {
            System.err.println("Error getting clubs: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("clubs", new ArrayList<>());
        }
        
        model.addAttribute("student", student);
        return "students/debug";
    }
    
    @PostMapping("/students/clubs/join/{clubId}")
    public String joinClub(@PathVariable Long clubId, HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("=== JOIN CLUB REQUEST ===");
        System.out.println("Club ID: " + clubId);
        
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            System.out.println("No student in session, redirecting to login");
            return "redirect:/login";
        }
        
        System.out.println("Student: " + student.getUsername() + " (ID: " + student.getId() + ")");
        
        try {
            Optional<Club> clubOpt = clubService.getClubById(clubId);
            if (clubOpt.isPresent()) {
                Club club = clubOpt.get();
                System.out.println("Club found: " + club.getName() + " (ID: " + club.getId() + ")");
                
                // Check if student is already a member of this club
                boolean isAlreadyMember = clubService.isStudentMemberOfClub(student, club);
                System.out.println("Is already member: " + isAlreadyMember);
                
                if (isAlreadyMember) {
                    System.out.println("Student is already a member, redirecting with error");
                    redirectAttributes.addFlashAttribute("error", "You are already a member of this club!");
                    return "redirect:/students/clubs";
                }

                // Enforce maximum of two active club memberships per student
                int activeMembershipCount = clubService.getActiveMembershipsByStudent(student).size();
                if (activeMembershipCount >= 2) {
                    System.out.println("Student reached max club limit (2), blocking join");
                    redirectAttributes.addFlashAttribute("error", "You can only participate in one activity at a time. Please wait until your current activity ends.");
                    return "redirect:/students/clubs";
                }
                
                // Join the club
                System.out.println("Joining club...");
                ClubMembership membership = clubService.joinClub(student, club);
                System.out.println("Membership created with ID: " + membership.getId());
                
                // Award points for joining
                try {
                    studentService.addPointsToStudent(student.getId(), 100, "Joined club: " + club.getName());
                    redirectAttributes.addFlashAttribute("success", "Joined " + club.getName() + " and earned 100 points!");
                } catch (Exception pointsEx) {
                    System.err.println("Error awarding points on join: " + pointsEx.getMessage());
                }
                
                // Redirect to member page
                String redirectUrl = "redirect:/students/clubs/member/" + membership.getId();
                System.out.println("Redirecting to: " + redirectUrl);
                return redirectUrl;
            } else {
                System.out.println("Club not found for ID: " + clubId);
                redirectAttributes.addFlashAttribute("error", "Club not found!");
                return "redirect:/students/clubs";
            }
        } catch (Exception e) {
            System.err.println("Error joining club: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error joining club: " + e.getMessage());
            return "redirect:/students/clubs";
        }
    }
    
    @PostMapping("/students/clubs/quit/{clubId}")
    public String quitClub(@PathVariable Long clubId, HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("=== QUIT CLUB REQUEST ===");
        System.out.println("Club ID: " + clubId);
        
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            System.out.println("No student in session, redirecting to login");
            return "redirect:/login";
        }
        
        System.out.println("Student: " + student.getUsername() + " (ID: " + student.getId() + ")");
        
        try {
            Optional<Club> clubOpt = clubService.getClubById(clubId);
            if (clubOpt.isPresent()) {
                Club club = clubOpt.get();
                System.out.println("Club found: " + club.getName() + " (ID: " + club.getId() + ")");
                
                // Check if student is actually a member
                boolean isMember = clubService.isStudentMemberOfClub(student, club);
                System.out.println("Is member: " + isMember);
                
                if (!isMember) {
                    System.out.println("Student is not a member, redirecting with error");
                    redirectAttributes.addFlashAttribute("error", "You are not a member of this club!");
                    return "redirect:/students/clubs";
                }

                // Prevent leaving if student has 0 points (no points to reduce)
                Integer currentPoints = studentService.getStudentPoints(student.getId());
                if (currentPoints != null && currentPoints == 0) {
                    System.out.println("Student has 0 points; blocking leave action");
                    redirectAttributes.addFlashAttribute("error", "You cannot leave the club. Your current points must be > 0.");
                    return "redirect:/students/clubs";
                }
                
                // Quit the club
                System.out.println("Quitting club...");
                clubService.quitClub(student, club);
                System.out.println("Student successfully quit the club");
                
                // Subtract fixed 100 points for leaving
                try {
                    studentService.addPointsToStudent(student.getId(), -100, "Left club: " + club.getName());
                    redirectAttributes.addFlashAttribute("success", "Left " + club.getName() + " and 100 points were deducted.");
                } catch (Exception pointsEx) {
                    System.err.println("Error deducting points on leave: " + pointsEx.getMessage());
                }
                
                redirectAttributes.addFlashAttribute("success", "You have successfully quit " + club.getName());
                return "redirect:/students/clubs";
            } else {
                System.out.println("Club not found for ID: " + clubId);
                redirectAttributes.addFlashAttribute("error", "Club not found!");
                return "redirect:/students/clubs";
            }
        } catch (Exception e) {
            System.err.println("Error quitting club: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error quitting club: " + e.getMessage());
            return "redirect:/students/clubs";
        }
    }
    
    @GetMapping("/students/clubs/member/{membershipId}")
    public String memberPage(@PathVariable Long membershipId, Model model, HttpSession session) {
        System.out.println("=== MEMBER PAGE REQUEST ===");
        System.out.println("Membership ID: " + membershipId);
        
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            System.out.println("No student in session, redirecting to login");
            return "redirect:/login";
        }
        
        System.out.println("Student: " + student.getUsername() + " (ID: " + student.getId() + ")");
        
        try {
            Optional<ClubMembership> membershipOpt = clubService.getMembershipById(membershipId);
            if (membershipOpt.isPresent()) {
                ClubMembership membership = membershipOpt.get();
                Club club = membership.getClub();
                
                System.out.println("Membership found: " + membership.getId());
                System.out.println("Club: " + club.getName());
                
                // Format the joined date for display
                String formattedJoinedDate = membership.getJoinedAt().format(java.time.format.DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a"));
                
                model.addAttribute("membership", membership);
                model.addAttribute("club", club);
                model.addAttribute("student", student);
                model.addAttribute("formattedJoinedDate", formattedJoinedDate);
                
                System.out.println("Returning member.jsp");
                return "students/member";
            } else {
                System.err.println("Membership not found for ID: " + membershipId);
                return "redirect:/students/clubs";
            }
        } catch (Exception e) {
            System.err.println("Error loading member page: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/students/clubs";
        }
    }
    
    @GetMapping("/students/clubs/detail/{clubId}")
    public String clubDetail(@PathVariable Long clubId, Model model, HttpSession session) {
        System.out.println("=== CLUB DETAIL PAGE REQUEST ===");
        System.out.println("Club ID: " + clubId);
        
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            System.out.println("No student in session, redirecting to login");
            return "redirect:/login";
        }
        
        System.out.println("Student: " + student.getUsername() + " (ID: " + student.getId() + ")");
        
        try {
            Optional<Club> clubOpt = clubService.getClubById(clubId);
            if (clubOpt.isPresent()) {
                Club club = clubOpt.get();
                
                System.out.println("Club found: " + club.getName());
                
                // Check if student has any membership (active or inactive) for this club
                Optional<ClubMembership> membershipOpt = clubService.getMembershipByStudentAndClub(student, club);
                boolean hasAnyMembership = membershipOpt.isPresent();
                boolean isActiveMember = clubService.isStudentMemberOfClub(student, club);
                
                model.addAttribute("club", club);
                model.addAttribute("student", student);
                model.addAttribute("membershipStatus", isActiveMember);
                model.addAttribute("hasAnyMembership", hasAnyMembership);
                model.addAttribute("canRejoin", hasAnyMembership && !isActiveMember);
                
                System.out.println("Returning clubdetail.jsp");
                return "students/clubdetail";
            } else {
                System.err.println("Club not found for ID: " + clubId);
                return "redirect:/students/clubs";
            }
        } catch (Exception e) {
            System.err.println("Error loading club detail page: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/students/clubs";
        }
    }
    
 @GetMapping("/events")
    public String studentEvents(Model model, HttpSession session, jakarta.servlet.http.HttpServletRequest request) {
        try {
            Student student = (Student) session.getAttribute("user");
            if (student != null) {
                model.addAttribute("student", student);
            }
            List<Event> events = eventService.getAllEvents();
            LocalDateTime now = LocalDateTime.now();

            Map<Long, Map<String, Object>> eventJoinStatus = new HashMap<>();
            Map<Long, Map<String, Object>> eventStatus = new HashMap<>();
            Map<Long, Boolean> joinedEventMap = new HashMap<>();

            // Read flash attribute for immediate feedback
            Long justJoinedId = null;
            try {
                java.util.Map<String, ?> flash = org.springframework.web.servlet.support.RequestContextUtils.getInputFlashMap(request);
                if (flash != null && flash.get("joinedEventId") instanceof Long) {
                    justJoinedId = (Long) flash.get("joinedEventId");
                }
            } catch (Exception ignored) {}

            // Mark already joined for current student
            if (student != null) {
                for (Event e : events) {
                    boolean joined = false;
                    try {
                        joined = eventService.hasAnyParticipation(student, e);
                    } catch (Exception ignored) {}
                    if (joined) joinedEventMap.put(e.getId(), true);
                }
                if (justJoinedId != null) {
                    joinedEventMap.put(justJoinedId, true);
                }
            }

            for (Event event : events) {
                Map<String, Object> join = new HashMap<>();
                Map<String, Object> status = new HashMap<>();

                boolean canJoin = false;
                String primaryLabel = "Join Closed";
                String secondaryLabel = null;
                String state = "ended";

                if (event.getStartTime() != null && event.getEndTime() != null) {
                    LocalDate date = event.getStartTime().toLocalDate();
                    LocalTime start = event.getStartTime().toLocalTime();
                    LocalTime end = event.getEndTime().toLocalTime();
                    LocalDateTime startWindow = LocalDateTime.of(date, start).minusMinutes(30);
                    LocalDateTime endJoinDeadline = LocalDateTime.of(date, end).minusMinutes(30);

                    if (now.isBefore(startWindow)) {
                        Duration untilOpen = Duration.between(now, startWindow).plusMinutes(1);
                        primaryLabel = "Join available in " + formatDurationShort(untilOpen);
                        canJoin = false;
                        state = "upcoming";
                    } else if (!now.isAfter(endJoinDeadline)) {
                        Duration untilClose = Duration.between(now, endJoinDeadline);
                        primaryLabel = "Join now";
                        secondaryLabel = "Join closes in " + formatDurationShort(untilClose);
                        canJoin = true;
                        state = "ongoing";
                    } else {
                        primaryLabel = "Join Disabled";
                        canJoin = false;
                        state = "ended";
                    }
                }

                boolean alreadyJoined = joinedEventMap.getOrDefault(event.getId(), false);
                if (alreadyJoined) {
                    join.put("canJoin", false);
                    join.put("primaryLabel", "Already Joined");
                    join.put("secondaryLabel", null);
                } else {
                    join.put("canJoin", canJoin);
                    join.put("primaryLabel", primaryLabel);
                    join.put("secondaryLabel", secondaryLabel);
                }
                status.put("status", state);

                eventJoinStatus.put(event.getId(), join);
                eventStatus.put(event.getId(), status);
            }


            model.addAttribute("events", events);
            model.addAttribute("eventJoinStatus", eventJoinStatus);
            model.addAttribute("eventStatus", eventStatus);
            model.addAttribute("joinedEventMap", joinedEventMap);
            return "students/eventjoin";
        } catch (Exception e) {
            System.err.println("Error loading events: " + e.getMessage());
            model.addAttribute("events", new ArrayList<>());
            model.addAttribute("eventJoinStatus", new HashMap<>());
            model.addAttribute("eventStatus", new HashMap<>());
            model.addAttribute("joinedEventMap", new HashMap<>());
            return "students/eventjoin";
        }
    }
    

    
    @GetMapping("/events/view/{id}")
    public String viewEventForStudent(@PathVariable Long id, Model model, HttpSession session) {
        try {
            Student student = (Student) session.getAttribute("user");
            if (student == null) {
                return "redirect:/login";
            }
            
            return eventService.getEventById(id)
                    .map(event -> {
                        try {
                            model.addAttribute("event", event);
                            
                            // Check time window and registration state with proper null checks
                            LocalDateTime now = LocalDateTime.now();
                            boolean isJoinWindow = false;
                            boolean hasStarted = false;
                            boolean hasEnded = false;
                            
                            if (event.getStartTime() != null && event.getEndTime() != null) {
                                hasStarted = !now.isBefore(event.getStartTime());
                                hasEnded = now.isAfter(event.getEndTime());
                                isJoinWindow = hasStarted && !hasEnded;
                            }
                            
                            model.addAttribute("isJoinWindow", isJoinWindow);
                            model.addAttribute("hasStarted", hasStarted);
                            model.addAttribute("hasEnded", hasEnded);
                            model.addAttribute("currentTime", now);
                            model.addAttribute("isRegistered", eventService.getPendingParticipationsByEvent(event).stream()
                                    .anyMatch(p -> p.getStudent().getId().equals(student.getId())));
                            return "events/view";
                        } catch (Exception e) {
                            System.err.println("Error processing event " + event.getId() + ": " + e.getMessage());
                            return "redirect:/events";
                        }
                    })
                    .orElse("redirect:/events");
        } catch (Exception e) {
            System.err.println("Error viewing event " + id + ": " + e.getMessage());
            return "redirect:/events";
        }
    }
    

    @PostMapping("/events/register/{id}")
    public String registerForEvent(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }

        try {
            Optional<Event> eventOpt = eventService.getEventById(id);
            if (eventOpt.isPresent()) {
                Event event = eventOpt.get();
                
                // Check if student has any active event participation (either PENDING or APPROVED)
                if (eventParticipationRepository.hasActiveEventParticipation(student.getId(), EventParticipation.ParticipationStatus.APPROVED) ||
                    eventParticipationRepository.hasActiveEventParticipation(student.getId(), EventParticipation.ParticipationStatus.PENDING)) {
                    redirectAttributes.addFlashAttribute("error", "You can only participate in one event at a time. Please wait until your current event ends.");
                    return "redirect:/events";
                }
                
                // Register for the event if no active participations
                EventParticipation participation = eventService.registerForEvent(student, event);
                redirectAttributes.addFlashAttribute("joinedEventId", id);
                redirectAttributes.addFlashAttribute("success", "Successfully registered for event: " + event.getName());
            } else {
                redirectAttributes.addFlashAttribute("error", "Event not found!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error registering for event: " + e.getMessage());
        }
        return "redirect:/events";
    }
    
    // Manual trigger for point awarding (for testing)
    @GetMapping("/admin/manual-award-points")
    public String manualAwardPoints(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        eventService.manuallyAwardPointsForEndedEvents();
        return "redirect:/admin/events";
    }
    
    // Force award points for all ended events (admin use)
    @PostMapping("/admin/force-award-points")
    public String forceAwardPoints(HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            eventService.forceAwardPointsForAllEndedEvents();
            redirectAttributes.addFlashAttribute("success", "Force point awarding completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error in force point awarding: " + e.getMessage());
        }
        
        return "redirect:/admin/event-participations";
    }
    
    // Check all student points (for debugging)
    @GetMapping("/admin/check-student-points")
    public String checkStudentPoints(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        eventService.checkAllStudentPoints();
        return "redirect:/admin/events";
    }
    
    // Check pending point awards (for debugging)
    @GetMapping("/admin/check-pending-points")
    public String checkPendingPointAwards(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        eventService.checkPendingPointAwards();
        return "redirect:/admin/events";
    }
  
// Get event participation status (for debugging)
    @GetMapping("/admin/event-participation-status")
    public String getEventParticipationStatus(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            Map<String, Object> status = eventService.getEventParticipationStatus();
            model.addAttribute("status", status);
            return "admin/event-participation-status";
        } catch (Exception e) {
            model.addAttribute("error", "Error getting status: " + e.getMessage());
            return "admin/event-participation-status";
        }
    }    // Update student dashboard to include clubs and events
    @GetMapping("/students/dashboard/{id}")
    public String studentDashboard(@PathVariable Long id, Model model) {
        return studentService.getStudentById(id)
                .map(student -> {
                    model.addAttribute("student", student);
                    
                    // Get student's reward exchanges
                    List<RewardExchange> recentExchanges = rewardExchangeService.getRecentExchangesByStudent(student);
                    model.addAttribute("recentExchanges", recentExchanges);
                    
                    // Get available rewards based on student's points
                    // Changed from getAllRewards() to getActiveRewards() to only show active rewards
                    List<Reward> availableRewards = rewardService.getActiveRewards().stream()
                            .filter(reward -> student.getPoints() >= reward.getPointValue())
                            .collect(Collectors.toList());
                    model.addAttribute("availableRewards", availableRewards);
                    
                    // Calculate total points spent on rewards
                    Integer totalPointsSpent = rewardExchangeService.getTotalPointsSpentByStudent(student);
                    model.addAttribute("totalPointsSpent", totalPointsSpent);
                    
                    // Prepare data for points history chart
                    Map<String, Integer> monthlyPoints = new LinkedHashMap<>();
                    LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
                    
                    // Initialize with last 6 months
                    for (int i = 5; i >= 0; i--) {
                        LocalDateTime month = LocalDateTime.now().minusMonths(i);
                        String monthLabel = month.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                        monthlyPoints.put(monthLabel, 0);
                    }
                    
                    model.addAttribute("pointsChartLabels", new ArrayList<>(monthlyPoints.keySet()));
                    model.addAttribute("pointsChartData", new ArrayList<>(monthlyPoints.values()));
                    
                    // Add clubs and events data
                    List<Club> clubs = clubService.getAllClubs();
                    List<ClubMembership> studentMemberships = clubService.getActiveMembershipsByStudent(student);
                    model.addAttribute("clubsCount", studentMemberships.size());
                    model.addAttribute("eventsCount", eventService.getAllEvents().size());
                    model.addAttribute("clubs", clubs);
                    
                    // Get club-specific activities for student's joined clubs
                    Map<Club, List<Activity>> clubActivities = new HashMap<>();
                    for (ClubMembership membership : studentMemberships) {
                        Club club = membership.getClub();
                        List<Activity> activities = activityService.getActivitiesByClub(club);
                        clubActivities.put(club, activities);
                    }
                    model.addAttribute("clubActivities", clubActivities);
                    model.addAttribute("studentMemberships", studentMemberships);
                    
                    // Format events with formatted dates for JSP compatibility
                    List<Event> events = eventService.getAllEvents();
                    model.addAttribute("upcomingEvents", events);
                    
                    // Add rewards count
                    model.addAttribute("rewardsCount", availableRewards.size());
                    
                    return "students/dashboard";
                })
                .orElse("redirect:/students");
    }

    @GetMapping("/students/rewards/catalog")
    public String rewardCatalog(Model model, HttpSession session, 
                                @RequestParam(required = false) String search, 
                                @RequestParam(required = false) String filter) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        List<Reward> rewards;
        
        if (search != null && !search.isEmpty()) {
            rewards = rewardService.searchRewardsByKeyword(search);
        } else if ("available".equals(filter)) {
            rewards = rewardService.getActiveRewards().stream()
                .filter(reward -> student.getPoints() >= reward.getPointValue())
                .collect(Collectors.toList());
        } else {
            rewards = rewardService.getActiveRewards();
        }
        
        model.addAttribute("student", student);
        model.addAttribute("rewards", rewards);
        model.addAttribute("search", search);
        model.addAttribute("filter", filter);
        
        return "students/rewards/catalog";
    }
    
    // Exchange a reward
    @PostMapping("/students/rewards/exchange/{rewardId}")
    public String exchangeReward(@PathVariable Long rewardId, HttpSession session, RedirectAttributes redirectAttributes) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        try {
            // Check if student has enough points
            if (!studentService.hasEnoughPointsForReward(student.getId(), rewardId)) {
                redirectAttributes.addFlashAttribute("error", "You don't have enough points for this reward.");
                return "redirect:/students/rewards/catalog";
            }
            
            // Process the exchange
            RewardExchange exchange = rewardExchangeService.exchangeReward(student.getId(), rewardId);
            redirectAttributes.addFlashAttribute("success", "Reward exchanged successfully!");
            
            // Update the student in the session with the latest data from the database
            Student updatedStudent = studentService.getStudentById(student.getId()).orElse(student);
            session.setAttribute("user", updatedStudent);
            
            return "redirect:/students/rewards/history";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error exchanging reward: " + e.getMessage());
            return "redirect:/students/rewards/catalog";
        }
    }
    
    // View exchange history
    @GetMapping("/students/rewards/history")
    public String rewardExchangeHistory(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        // Get student's exchange history
        List<RewardExchange> exchanges = rewardExchangeService.getExchangesByStudent(student);
        
        model.addAttribute("student", student);
        model.addAttribute("exchanges", exchanges);
        
        return "students/rewards/history";
    }
    


    @GetMapping("/students/profile")
    public String studentProfile(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("student", student);
        return "students/profile";
    }
        @GetMapping("/students/dashboard")
    public String studentDashboardCurrent(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        // Redirect to the dashboard with the student's ID
        return "redirect:/students/dashboard/" + student.getId();
    }
    @PostMapping("/students/profile/update")
    public String updateStudentProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String department,
            @RequestParam int year,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        // Get current student from session
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        // Get fresh student data from database
        Optional<Student> freshStudentOpt = studentService.getStudentById(student.getId());
        if (!freshStudentOpt.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Student not found");
            return "redirect:/students/profile";
        }
        
        Student freshStudent = freshStudentOpt.get();
        
        // Update basic profile information
        freshStudent.setFirstName(firstName);
        freshStudent.setLastName(lastName);
        freshStudent.setEmail(email);
        freshStudent.setDepartment(department);
        freshStudent.setYear(year);
        
        // Handle password change if requested
        boolean passwordChangeRequested = false;
        
        // Check if any password field is filled
        if (currentPassword != null && !currentPassword.isEmpty() ||
            newPassword != null && !newPassword.isEmpty() ||
            confirmPassword != null && !confirmPassword.isEmpty()) {
            
            passwordChangeRequested = true;
            
            // Validate all password fields are provided
            if (currentPassword == null || currentPassword.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Current password is required to change password");
                return "redirect:/students/profile";
            }
            
            if (newPassword == null || newPassword.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "New password cannot be empty");
                return "redirect:/students/profile";
            }
            
            if (confirmPassword == null || confirmPassword.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please confirm your new password");
                return "redirect:/students/profile";
            }
            
            // Verify current password
            String hashedCurrentPassword = studentService.hashPassword(currentPassword);
            if (!freshStudent.getPassword().equals(hashedCurrentPassword)) {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
                return "redirect:/students/profile";
            }
            
            // Verify new password meets requirements
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters long");
                return "redirect:/students/profile";
            }
            
            // Verify new password matches confirmation
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New password and confirmation do not match");
                return "redirect:/students/profile";
            }
            
            // Hash and set new password
            String hashedNewPassword = studentService.hashPassword(newPassword);
            freshStudent.setPassword(hashedNewPassword);
        }
        
        // Save updated student
        studentService.saveStudent(freshStudent);
        
        // Update session with fresh student data
        session.setAttribute("user", freshStudent);
        
        // Set appropriate success message
        if (passwordChangeRequested) {
            redirectAttributes.addFlashAttribute("success", "Profile and password updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully");
        }
        
        return "redirect:/students/profile";
    }

    @GetMapping("/students/activities")
    public String studentActivitiesView(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        // Refresh student from DB to ensure latest points and details
        Student freshStudent = studentService.getStudentById(student.getId()).orElse(student);
        session.setAttribute("user", freshStudent);
        
        // Get student's club memberships
        List<ClubMembership> memberships = clubService.getActiveMembershipsByStudent(freshStudent);
        
        // Get activities for each club the student is a member of
        Map<Club, List<Activity>> clubActivities = new HashMap<>();
        Map<Long, Map<String, Object>> activityJoinStatus = new HashMap<>();
        Map<Long, Boolean> joinedActivityMap = new HashMap<>();

        // Build a quick lookup of activities the student has already joined (any status)
        try {
            List<ActivityParticipation> myParticipations = activityParticipationService.getParticipationsByStudent(freshStudent);
            for (ActivityParticipation p : myParticipations) {
                if (p.getActivity() != null) {
                    joinedActivityMap.put(p.getActivity().getId(), true);
                }
            }
        } catch (Exception ignored) {
        }
        for (ClubMembership membership : memberships) {
            Club club = membership.getClub();
            List<Activity> activities = activityService.getActivitiesByClub(club);
            clubActivities.put(club, activities);
            // Compute join window status per activity
            for (Activity activity : activities) {
                Map<String, Object> status = new HashMap<>();
                boolean canJoin = false;
                String primaryLabel = "Join Closed";
                String secondaryLabel = null;
                try {
                    // Expecting formats: yyyy-MM-dd and HH:mm
                    DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
                    LocalDate date = LocalDate.parse(activity.getClubDate(), dateFmt);
                    LocalTime start = LocalTime.parse(activity.getStartTime(), timeFmt);
                    LocalTime end = LocalTime.parse(activity.getEndTime(), timeFmt);
                    LocalDateTime startWindow = LocalDateTime.of(date, start).minusMinutes(30);
                    LocalDateTime endJoinDeadline = LocalDateTime.of(date, end).minusMinutes(30);
                    LocalDateTime now = LocalDateTime.now();
//Available minutes calculate
                    if (now.isBefore(startWindow)) {
                        Duration untilOpen = Duration.between(now, startWindow).plusMinutes(1);
                        primaryLabel = "Join available in " + formatDurationShort(untilOpen);
                        canJoin = false;
                    } else if (!now.isAfter(endJoinDeadline)) {
                        // In join window
                        Duration untilClose = Duration.between(now, endJoinDeadline);
                        primaryLabel = "Join now";
                        secondaryLabel = "Join closes in " + formatDurationShort(untilClose);
                        canJoin = true;
                    } else {
                        // After deadline
                        primaryLabel = "Join Disabled";
                        canJoin = false;
                    }
                } catch (Exception e) {
                    // If parsing fails, keep defaults (closed)
                    primaryLabel = "Join Didabled";
                    canJoin = false;
                }

                status.put("canJoin", canJoin);
                status.put("primaryLabel", primaryLabel);
                status.put("secondaryLabel", secondaryLabel);
                activityJoinStatus.put(activity.getId(), status);
            }
        }
        
        model.addAttribute("student", freshStudent);
        model.addAttribute("memberships", memberships);
        model.addAttribute("clubActivities", clubActivities);
        model.addAttribute("activityJoinStatus", activityJoinStatus);
        model.addAttribute("joinedActivityMap", joinedActivityMap);
        
        return "students/activities";
    }

    private String formatDurationShort(Duration duration) {
        long totalSeconds = duration.getSeconds();
        if (totalSeconds < 0) totalSeconds = 0;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        if (hours > 0 && minutes > 0) {
            return hours + "h " + minutes + "m";
        } else if (hours > 0) {
            return hours + "h";
        } else {
            return minutes + "m";
        }
    }
    
    @PostMapping("/students/activities/join/{activityId}")
    public String joinActivity(@PathVariable Long activityId, HttpSession session, RedirectAttributes redirectAttributes) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<Activity> activityOpt = activityService.getActivityById(activityId);
            if (activityOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Activity not found");
                return "redirect:/students/activities";
            }
            
            Activity activity = activityOpt.get();
            
            // Check if student is a member of the club that offers this activity
            Club activityClub = activity.getClub();
            if (activityClub != null) {
                boolean isMember = clubService.getActiveMembershipsByStudent(student).stream()
                        .anyMatch(m -> m.getClub().getId().equals(activityClub.getId()));
                
                if (!isMember) {
                    redirectAttributes.addFlashAttribute("error", "You must be a member of " + activityClub.getName() + " to join this activity");
                    return "redirect:/students/activities";
                }
            }
            
            // Join the activity - this will now check if student already has an active participation
            activityParticipationService.participateInActivity(student, activity);
            redirectAttributes.addFlashAttribute("success", "Successfully joined the activity. Waiting for approval.");
            
        } catch (IllegalStateException e) {
            // This will catch the error when student already has an active participation
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to join activity: " + e.getMessage());
        }
        
        return "redirect:/students/activities";
    }
    
    @GetMapping("/students/activities/participations")
    public String studentParticipations(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        List<ActivityParticipation> participations = activityParticipationService.getParticipationsByStudent(student);
        model.addAttribute("student", student);
        model.addAttribute("participations", participations);
        
        return "students/participations";
    }

    @GetMapping("/students/eventparticipation")
    public String studentEventParticipations(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        List<EventParticipation> participations = eventService.getParticipationsByStudent(student);
        model.addAttribute("student", student);
        model.addAttribute("participations", participations);
        
        return "students/eventparticipation";
    }

    // Admin Attendance Management
    @GetMapping("/admin/attendances")
    public String attendances(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        List<Attendance> attendances = attendanceService.getAllAttendances();
        
        // Convert LocalDateTime to Date for JSP compatibility
        attendances.forEach(attendance -> {
            if (attendance.getCreatedAt() != null) {
                model.addAttribute("createdAt_" + attendance.getId(), 
                    DateTimeUtil.toDate(attendance.getCreatedAt()));
            }
            if (attendance.getUpdatedAt() != null) {
                model.addAttribute("updatedAt_" + attendance.getId(), 
                    DateTimeUtil.toDate(attendance.getUpdatedAt()));
            }
        });
        
        model.addAttribute("attendances", attendances);
        return "admin/attendances/list";
    }
    
    @GetMapping("/admin/attendances/create")
    public String createAttendanceForm(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("attendance", new Attendance());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("months", Month.values());
        model.addAttribute("currentYear", java.time.LocalDate.now().getYear());
        return "admin/attendances/form";
    }
    
    @PostMapping("/admin/attendances/save")
    public String saveAttendance(@RequestParam("studentId") Long studentId,
                               @RequestParam("month") Month month,
                               @RequestParam("year") Integer year,
                               @RequestParam("attendancePercentage") Double attendancePercentage,
                               HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        // Validate attendance percentage (0-100)
        if (attendancePercentage < 0 || attendancePercentage > 100) {
            return "redirect:/admin/attendances/create?error=Invalid attendance percentage";
        }
        
        // Get student
        Optional<Student> studentOpt = studentService.getStudentById(studentId);
        if (!studentOpt.isPresent()) {
            return "redirect:/admin/attendances/create?error=Student not found";
        }
        
        // Create or update attendance record
        attendanceService.createOrUpdateAttendance(
            studentOpt.get(), month, year, attendancePercentage, admin);
        
        return "redirect:/admin/attendances";
    }
    
    @GetMapping("/admin/attendances/edit/{id}")
    public String editAttendanceForm(@PathVariable Long id, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        return attendanceService.getAttendanceById(id)
                .map(attendance -> {
                    model.addAttribute("attendance", attendance);
                    model.addAttribute("students", studentService.getAllStudents());
                    model.addAttribute("months", Month.values());
                    model.addAttribute("currentYear", java.time.LocalDate.now().getYear());
                    return "admin/attendances/form";
                })
                .orElse("redirect:/admin/attendances");
    }
    
    @GetMapping("/admin/attendances/delete/{id}")
    public String deleteAttendance(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        attendanceService.deleteAttendance(id);
        return "redirect:/admin/attendances";
    }
    
    @GetMapping("/admin/attendances/award-points/{id}")
    public String awardAttendancePoints(@PathVariable Long id, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        attendanceService.awardPointsForAttendance(id);
        return "redirect:/admin/attendances";
    }
    
    @GetMapping("/admin/attendances/calculate-points")
    public String calculateAttendancePoints(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        attendanceService.calculateAndAwardPoints();
        return "redirect:/admin/attendances";
    }
    
    // Semester Management
    @GetMapping("/admin/semesters")
    public String listSemesters(Model model) {
        model.addAttribute("semesters", semesterService.getAllSemesters());
        return "admin/semesters/list";
    }

    @GetMapping("/admin/semesters/new")
    public String newSemesterForm(Model model) {
        model.addAttribute("semester", new Semester());
        model.addAttribute("semesterNames", Semester.getAllSemesterNames());
        return "admin/semesters/form";
    }

    @PostMapping("/admin/semesters/save")
    public String saveSemester(@ModelAttribute Semester semester, RedirectAttributes redirectAttributes, Model model) {
        // The @ModelAttribute will automatically bind the checkbox value
        
        try {
            // Check if a semester with the same name and year already exists
            Optional<Semester> existingSemester = semesterService.getSemesterByNameAndYear(semester.getName(), semester.getYear());
            
            if (existingSemester.isPresent() && (semester.getId() == null || !semester.getId().equals(existingSemester.get().getId()))) {
                // Duplicate found and it's not the same record being edited
                model.addAttribute("semester", semester);
                model.addAttribute("semesterNames", Semester.getAllSemesterNames());
                model.addAttribute("error", "A semester with this name and year already exists");
                return "admin/semesters/form";
            }
            
            semesterService.saveSemester(semester);
            redirectAttributes.addFlashAttribute("message", "Semester saved successfully");
            return "redirect:/admin/semesters";
        } catch (Exception e) {
            // Handle other exceptions
            model.addAttribute("semester", semester);
            model.addAttribute("semesterNames", Semester.getAllSemesterNames());
            model.addAttribute("error", "Error saving semester: " + e.getMessage());
            return "admin/semesters/form";
        }
    }

    @GetMapping("/admin/semesters/edit/{id}")
    public String editSemesterForm(@PathVariable Long id, Model model) {
        Optional<Semester> semester = semesterService.getSemesterById(id);
        if (semester.isPresent()) {
            model.addAttribute("semester", semester.get());
            model.addAttribute("semesterNames", Semester.getAllSemesterNames());
            return "admin/semesters/form";
        }
        return "redirect:/admin/semesters";
    }

    @GetMapping("/admin/semesters/delete/{id}")
    public String deleteSemester(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        semesterService.deleteSemester(id);
        redirectAttributes.addFlashAttribute("message", "Semester deleted successfully");
        return "redirect:/admin/semesters";
    }

    // Semester Grade Management
    @GetMapping("/admin/semester-grades")
    public String listSemesterGrades(Model model) {
        List<SemesterGrade> grades = semesterGradeService.getAllSemesterGrades();
        
        // Convert LocalDateTime to Date for JSP compatibility
        grades.forEach(grade -> {
            if (grade.getCreatedAt() != null) {
                model.addAttribute("createdAt_" + grade.getId(), 
                    DateTimeUtil.toDate(grade.getCreatedAt()));
            }
        });
        
        model.addAttribute("semesterGrades", grades);
        return "admin/semester-grades/list";
    }

    @GetMapping("/admin/semester-grades/new")
    public String newSemesterGradeForm(Model model) {
        model.addAttribute("semesterGrade", new SemesterGrade());
        model.addAttribute("students", studentService.getAllStudents());
        model.addAttribute("semesters", semesterService.getAllSemesters());
        return "admin/semester-grades/form";
    }

    @PostMapping("/admin/semester-grades/save")
    public String saveSemesterGrade(@ModelAttribute SemesterGrade semesterGrade, 
                                  @RequestParam Long studentId,
                                  @RequestParam Long semesterId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        // Get the current admin from the session
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        // Get the student and semester
        Optional<Student> student = studentService.getStudentById(studentId);
        Optional<Semester> semester = semesterService.getSemesterById(semesterId);
        
        if (student.isPresent() && semester.isPresent()) {
            // Create or update the semester grade
            SemesterGrade grade = semesterGradeService.createOrUpdateSemesterGrade(
                student.get(), semester.get(), semesterGrade.getGpa(), admin);
            
            redirectAttributes.addFlashAttribute("message", "Semester grade saved successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Student or semester not found");
        }
        
        return "redirect:/admin/semester-grades";
    }

    @GetMapping("/admin/semester-grades/edit/{id}")
    public String editSemesterGradeForm(@PathVariable Long id, Model model) {
        Optional<SemesterGrade> semesterGrade = semesterGradeService.getSemesterGradeById(id);
        if (semesterGrade.isPresent()) {
            model.addAttribute("semesterGrade", semesterGrade.get());
            model.addAttribute("students", studentService.getAllStudents());
            model.addAttribute("semesters", semesterService.getAllSemesters());
            return "admin/semester-grades/form";
        }
        return "redirect:/admin/semester-grades";
    }

    @GetMapping("/admin/semester-grades/delete/{id}")
    public String deleteSemesterGrade(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        semesterGradeService.deleteSemesterGrade(id);
        redirectAttributes.addFlashAttribute("message", "Semester grade deleted successfully");
        return "redirect:/admin/semester-grades";
    }

    @GetMapping("/admin/semester-grades/award-points/{id}")
    public String awardPointsForGrade(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        SemesterGrade grade = semesterGradeService.awardPointsForGrade(id);
        if (grade != null) {
            redirectAttributes.addFlashAttribute("message", "Points awarded successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to award points");
        }
        return "redirect:/admin/semester-grades";
    }

    @GetMapping("/admin/semester-grades/award-all-points")
    public String awardAllPoints(RedirectAttributes redirectAttributes) {
        semesterGradeService.calculateAndAwardPoints();
        redirectAttributes.addFlashAttribute("message", "Points awarded for all eligible grades");
        return "redirect:/admin/semester-grades";
    }

    
    // Admin Spinwheel Management
    @GetMapping("/admin/spinwheels")
    public String adminSpinwheels(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        List<SpinWheel> spinWheels = spinWheelService.getAllSpinWheels();
        model.addAttribute("spinWheels", spinWheels);
        return "admin/spinwheel";
    }
    
    @GetMapping("/admin/spinwheels/create")
    public String createSpinwheelForm(Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        SpinWheel spinWheel = new SpinWheel();
        model.addAttribute("spinWheel", spinWheel);
        return "admin/spinwheel-form";
    }
    
    @GetMapping("/admin/spinwheels/edit/{id}")
    public String editSpinwheelForm(@PathVariable Long id, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        Optional<SpinWheel> spinWheelOpt = spinWheelService.getSpinWheelById(id);
        if (spinWheelOpt.isPresent()) {
            SpinWheel spinWheel = spinWheelOpt.get();
            List<SpinWheelItem> items = spinWheelItemService.getItemsBySpinWheel(spinWheel);
            model.addAttribute("spinWheel", spinWheel);
            model.addAttribute("items", items);
            return "admin/spinwheel-form";
        }
        return "redirect:/admin/spinwheels";
    }
    
    @PostMapping("/admin/spinwheels/save")
    public String saveSpinwheel(@ModelAttribute SpinWheel spinWheel, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            if (spinWheel.getId() == null) {
                // Creating new spinwheel
                spinWheel.setCreatedBy(admin);
                spinWheelService.saveSpinWheel(spinWheel);
                redirectAttributes.addFlashAttribute("success", "Spinwheel created successfully!");
            } else {
                // Updating existing spinwheel
                Optional<SpinWheel> existingSpinWheelOpt = spinWheelService.getSpinWheelById(spinWheel.getId());
                if (existingSpinWheelOpt.isPresent()) {
                    SpinWheel existingSpinWheel = existingSpinWheelOpt.get();
                    // Preserve the original createdBy and createdAt
                    spinWheel.setCreatedBy(existingSpinWheel.getCreatedBy());
                    spinWheel.setCreatedAt(existingSpinWheel.getCreatedAt());
                    // Update the updatedAt timestamp
                    spinWheel.setUpdatedAt(java.time.LocalDateTime.now());
                    spinWheelService.saveSpinWheel(spinWheel);
                    redirectAttributes.addFlashAttribute("success", "Spinwheel updated successfully!");
                } else {
                    redirectAttributes.addFlashAttribute("error", "Spinwheel not found!");
                    return "redirect:/admin/spinwheels";
                }
            }
            return "redirect:/admin/spinwheels";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving spinwheel: " + e.getMessage());
            return "redirect:/admin/spinwheels";
        }
    }
    
    @PostMapping("/admin/spinwheels/activate/{id}")
    public String activateSpinwheel(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            spinWheelService.activateSpinWheel(id);
            redirectAttributes.addFlashAttribute("success", "Spinwheel activated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error activating spinwheel: " + e.getMessage());
        }
        return "redirect:/admin/spinwheels";
    }
    
    @PostMapping("/admin/spinwheels/deactivate/{id}")
    public String deactivateSpinwheel(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            spinWheelService.deactivateSpinWheel(id);
            redirectAttributes.addFlashAttribute("success", "Spinwheel deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deactivating spinwheel: " + e.getMessage());
        }
        return "redirect:/admin/spinwheels";
    }
    
    @PostMapping("/admin/spinwheels/delete/{id}")
    public String deleteSpinwheel(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            // Delete all associated spinwheel history and deduct points from students
            spinWheelHistoryService.deleteAllHistoryBySpinWheel(id);
            
            // Delete the spinwheel itself
            spinWheelService.deleteSpinWheel(id);
            redirectAttributes.addFlashAttribute("success", "Spinwheel and all associated history deleted successfully! Points have been deducted from students.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting spinwheel: " + e.getMessage());
        }
        return "redirect:/admin/spinwheels";
    }
    
    @PostMapping("/admin/spinwheel-history/delete/{id}")
    public String deleteSpinwheelHistory(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            // Delete the spinwheel history record and deduct points from student
            spinWheelHistoryService.deleteSpinWheelHistory(id);
            redirectAttributes.addFlashAttribute("success", "Spinwheel history deleted successfully! Points have been deducted from the student.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting spinwheel history: " + e.getMessage());
        }
        return "redirect:/admin/spinwheels";
    }
    
    // Spinwheel Item Management
    @GetMapping("/admin/spinwheels/{spinWheelId}/items/create")
    public String createSpinwheelItemForm(@PathVariable Long spinWheelId, Model model, HttpSession session) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        Optional<SpinWheel> spinWheelOpt = spinWheelService.getSpinWheelById(spinWheelId);
        if (spinWheelOpt.isPresent()) {
            SpinWheelItem item = new SpinWheelItem();
            item.setSpinWheel(spinWheelOpt.get());
            model.addAttribute("item", item);
            model.addAttribute("spinWheel", spinWheelOpt.get());
            return "admin/spinwheel-item-form";
        }
        return "redirect:/admin/spinwheels";
    }
    
    @PostMapping("/admin/spinwheels/items/save")
    public String saveSpinwheelItem(@ModelAttribute SpinWheelItem item, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            spinWheelItemService.saveSpinWheelItem(item);
            redirectAttributes.addFlashAttribute("success", "Spinwheel item saved successfully!");
            return "redirect:/admin/spinwheels/edit/" + item.getSpinWheel().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error saving item: " + e.getMessage());
            return "redirect:/admin/spinwheels";
        }
    }
    
    @PostMapping("/admin/spinwheels/items/delete/{id}")
    public String deleteSpinwheelItem(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("user");
        if (admin == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<SpinWheelItem> itemOpt = spinWheelItemService.getSpinWheelItemById(id);
            if (itemOpt.isPresent()) {
                Long spinWheelId = itemOpt.get().getSpinWheel().getId();
                spinWheelItemService.deleteSpinWheelItem(id);
                redirectAttributes.addFlashAttribute("success", "Item deleted successfully!");
                return "redirect:/admin/spinwheels/edit/" + spinWheelId;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting item: " + e.getMessage());
        }
        return "redirect:/admin/spinwheels";
    }
    
    // Student Spinwheel Endpoints
    @GetMapping("/students/spinwheel")
    public String studentSpinwheelList(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        List<SpinWheel> activeSpinWheels = spinWheelService.getActiveSpinWheels();
        model.addAttribute("spinWheels", activeSpinWheels);
        model.addAttribute("student", student);
        return "students/spinwheel-list";
    }
    
    @GetMapping("/students/spinwheel/{id}")
    public String studentSpinwheel(@PathVariable Long id, Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        Optional<SpinWheel> spinWheelOpt = spinWheelService.getSpinWheelById(id);
        if (spinWheelOpt.isPresent()) {
            SpinWheel spinWheel = spinWheelOpt.get();
            List<SpinWheelItem> items = spinWheelItemService.getItemsBySpinWheel(spinWheel);
            
            // Original behavior: only whether spun today (for messaging), count optional
            boolean hasSpunToday = spinWheelHistoryService.hasStudentSpunSpinWheelToday(student, spinWheel);
            int todaySpinCount = spinWheelHistoryService.getTodaySpinCount(student, spinWheel);
            
            model.addAttribute("spinWheel", spinWheel);
            model.addAttribute("items", items);
            model.addAttribute("student", student);
            model.addAttribute("hasSpunToday", hasSpunToday);
            model.addAttribute("todaySpinCount", todaySpinCount);
            return "students/spinwheel-platform";
        }
        return "redirect:/students/spinwheel";
    }
    
    @PostMapping("/students/spinwheel/{id}/spin")
    public String spinWheel(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Student student = (Student) session.getAttribute("user");
        if (student == null) {
            return "redirect:/login";
        }
        
        try {
            Optional<SpinWheel> spinWheelOpt = spinWheelService.getSpinWheelById(id);
            if (spinWheelOpt.isPresent()) {
                SpinWheel spinWheel = spinWheelOpt.get();
                
                // Original behavior: single spin per day
                if (spinWheelHistoryService.hasStudentSpunSpinWheelToday(student, spinWheel)) {
                    redirectAttributes.addFlashAttribute("error", "You have already spun this wheel today. Come back tomorrow!");
                    return "redirect:/students/spinwheel/" + id;
                }
                
                // Get random item based on probability weights
                List<SpinWheelItem> items = spinWheelItemService.getItemsBySpinWheel(spinWheel);
                if (items.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "No items available on this spinwheel.");
                    return "redirect:/students/spinwheel/" + id;
                }
                
                SpinWheelItem selectedItem = selectRandomItem(items);
                
                // Record the spin and award points
                SpinWheelHistory history = spinWheelHistoryService.recordSpin(student, spinWheel, selectedItem);
                
                // Update student in session
                Student updatedStudent = studentService.getStudentById(student.getId()).orElse(student);
                session.setAttribute("user", updatedStudent);
                
                redirectAttributes.addFlashAttribute("success", 
                    "Congratulations! You won " + selectedItem.getPointValue() + " points with " + selectedItem.getLabel() + "!");
                redirectAttributes.addFlashAttribute("spunItem", selectedItem);
                
                return "redirect:/students/spinwheel/" + id;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error spinning wheel: " + e.getMessage());
        }
        
        return "redirect:/students/spinwheel";
    }
    
    // Helper method to select random item based on probability weights
    private SpinWheelItem selectRandomItem(List<SpinWheelItem> items) {
        int totalWeight = items.stream().mapToInt(SpinWheelItem::getProbabilityWeight).sum();
        int random = (int) (Math.random() * totalWeight);
        
        for (SpinWheelItem item : items) {
            random -= item.getProbabilityWeight();
            if (random < 0) {
                return item;
            }
        }
        
        return items.get(0); // Fallback to first item
    }

}
