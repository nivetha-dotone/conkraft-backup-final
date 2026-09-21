package com.wfd.dot1.cwfm.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.wfd.dot1.cwfm.dto.AadharCheckDto;
import com.wfd.dot1.cwfm.dto.ApproveRejectGatePassDto;
import com.wfd.dot1.cwfm.dto.ApproverStatusDTO;
import com.wfd.dot1.cwfm.dto.ContractWorkmenReportDTO;
import com.wfd.dot1.cwfm.dto.GatePassActionDto;
import com.wfd.dot1.cwfm.dto.GatePassListingDto;
import com.wfd.dot1.cwfm.pojo.CmsContractorWC;
import com.wfd.dot1.cwfm.pojo.CmsGeneralMaster;
import com.wfd.dot1.cwfm.pojo.ContractWorkmenExportDto;
import com.wfd.dot1.cwfm.pojo.Contractor;
import com.wfd.dot1.cwfm.pojo.DeptMapping;
import com.wfd.dot1.cwfm.pojo.GatePassMain;
import com.wfd.dot1.cwfm.pojo.MasterUser;
import com.wfd.dot1.cwfm.pojo.PersonOrgLevel;
import com.wfd.dot1.cwfm.pojo.PrincipalEmployer;
import com.wfd.dot1.cwfm.pojo.Skill;
import com.wfd.dot1.cwfm.pojo.Trade;
import com.wfd.dot1.cwfm.pojo.WageDetailsDto;
import com.wfd.dot1.cwfm.pojo.Workorder;
import com.wfd.dot1.cwfm.pojo.Zone;

public interface WorkmenService {

	public List<PrincipalEmployer> getAllPrincipalEmployer(String userAccount);
	
	public List<Contractor> getAllContractorBasedOnPE(String unitId,String userId);

	public List<Workorder> getAllWorkordersBasedOnPEAndContractor(String unitId, String contractorId);

	public List<Trade> getAllTradesBasedOnPE(String unitId);

	public List<Skill> getAllSkill();

	public List<CmsGeneralMaster> getAllDepartmentAndSubDepartment(String userId);

	public List<MasterUser> getAllEicManager(String unitId,String deptId);

	public List<CmsContractorWC> getAllWCBasedOnPEAndCont(String unitId, String contractorId,String workorderId);

	public List<CmsGeneralMaster> getAllGenders();

	public List<CmsGeneralMaster> getAllGeneralMaster();

	public String saveGatePass(GatePassMain gatePassMain);

	public List<GatePassListingDto> getGatePassListingDetails(String unitId,String deptId,String userId,String gatePassTypeId,String type, List<PersonOrgLevel> contList);

	public List<GatePassListingDto> getGatePassListingForApprovers(String unitId,String deptId,MasterUser user, String gatePassTypeId,String type);

	public GatePassMain getIndividualContractWorkmenDetails(String gatePassId);

	public List<CmsGeneralMaster> getAllGeneralMasterForGatePass(GatePassMain gatePassMainObj);

	public String approveRejectGatePass(ApproveRejectGatePassDto dto);

	public String gatePassAction(GatePassActionDto dto);
	
	public List<GatePassListingDto> getGatePassActionListingDetails(String unitId,String deptId,String userId,String gatePassTypeId,String previousGatePassAction,String renewGatePassAction,String bulkRenewAction, List<PersonOrgLevel> contList);

	public List<GatePassListingDto> getWorkmenDetailBasedOnId(String gatePassId);

	public List<ApproverStatusDTO> getApprovalDetails(String gatePassId,String unitId,String gatePassTypeId, String Comments);

	public List<Contractor> getAllContractorForAdmin(String principalEmployerId);

	String draftGatePass(GatePassMain gatePassMain);

	String generateTransactionId();

	GatePassMain getIndividualContractWorkmenDraftDetails(String transactionId);

	public int getWorkFlowTYpe(String principalEmployer);

	List<GatePassListingDto> getRenewListingDetails(String userId, String gatePassTypeId, String gatePassStatus,
			String deptId, String unitId, List<PersonOrgLevel> contList);

	String renewGatePass(GatePassMain gatePassMain);


	List<GatePassListingDto> getGatePassActionListingForApprovers(String unitId, String deptId, MasterUser user,
			String gatePassTypeId);

	Map<String, Object> verifyOtpWithSurepass(String clientId, String otp);


	boolean isAadhaarExists(String aadhaarNumber);
	
	boolean isAadharDuplicate(String aadharNumber);

	public List<ContractWorkmenExportDto> getContractWorkmenExportData(String unitId);


	public boolean isAadharExists(String aadharNumber,String transactionId);


	

	public List<WageDetailsDto> getWageMasterExportData(String unitId);

	public String getOtherAadharLinkedToUan(String uan, String aadharNumber);

	public String getOtherAadharLinkedTopfNumber(String pfNumber, String aadharNumber);

	public void createDraftGatepass(String transactionId, String userId);

	List<GatePassListingDto> getGatePassActionListingDetailsForDashboardNav(String unitId, String deptId, String userId,
			String gatePassTypeId);

	public List<DeptMapping> getAllSkills(String unitId, String tradeId);

	public List<DeptMapping> getAllDepartmentsOnPE(String unitId);

	public List<DeptMapping> getAllSubDepartments(String unitId, String departmentId);
	
	String getTransactionIdByGPId(String gatepassid,String gatepasstypeid);
	GatePassMain getIndividualContractWorkmenDetailsByGatePassId(String gatepassid);

	 Map<String, String> getPreviousDocuments(String transactionId);

	public List<Map<String, Object>> getAllVersionedDocuments(String transactionId, Integer userId);

	public void saveRenewedDocuments(String transactionId, String valueOf, MultipartFile aadharFile,
			MultipartFile policeFile, MultipartFile profilePic, List<MultipartFile> additionalFiles,
			List<String> documentTypes,String filePath);

	public List<GatePassListingDto> getGatePassUnblockDeblackListingDetails(String principalEmployerId, String deptId,
			String roleId, String gatePassTypeId, String previousGatePassAction, String renewGatePassAction, List<PersonOrgLevel> contList);


	public String getAadharStatus(String aadharNumber);

	public AadharCheckDto  checkAadharUniqueness(String aadharNumber, String gatePassId, String transactionId);

	public GatePassMain getIndividualContractWorkmenDetailsByGatePassIdRenew(String gatePassId);

	public String saveWorkmenBulkUploadGatePass(GatePassMain gatePassMain);

	String saveProjectGatePass(GatePassMain gatePassMain);

	String getRenewTransactionIfExists(String gatePassId);

	public boolean  updateGatePassMainWithCancelReasoningTab(GatePassActionDto dto, MultipartFile exitFile, MultipartFile fnfFile,
			MultipartFile feedbackFile, MultipartFile rateManagerFile, MultipartFile locFile);

	public String getStateOnPrincipalEmployer(String principalEmployer);

	Map<String, Object> getMinimumWage(String principalEmployer, String stateId, String zone, String skill);

	public boolean updateGatePassMainWithReasoningTab(GatePassActionDto dto, MultipartFile attachmentOfReference);

	public List<Zone> getAllZonesBasedOnPE(String unitId);

	public Map<String, Object> getWorkmenDetailsByAadhar(String aadharNumber);

	List<ContractWorkmenReportDTO> getContractWorkmenReportData(String unitId, String contractorId, String startDate, String endDate);

	public List<ContractWorkmenReportDTO> getInactiveWorkmenReportData(String unitId, String contractorId);

	public List<ContractWorkmenReportDTO> getPoliceverificationWorkmenReportData(String unitId, String contractorId);

	public List<ContractWorkmenReportDTO> getPolicyExpiryWorkmenReportData(String unitId, String contractorId);

	List<ApproveRejectGatePassDto> getTrainingDetails(String unitId, String department);

	public List<ApproveRejectGatePassDto> getExistingTrainingRecords(String transactionId);

	public boolean isActionDoneToday(String gatePassId, Long gatePassTypeId);

	String saveFullTimeContractorGatePass(GatePassMain gatePassMain);

	public GatePassMain getFullTimeIndividualContractWorkmenDetails(String transactionId);

	public List<GatePassListingDto> getFullTimeContGatePassListingDetails(String principalEmployerId, String deptId,
			String valueOf, String status, String type, List<PersonOrgLevel> contList);
	
	String determineISMW(String address, String unitId);

	public String getStateByPincode(String pincode);

}	
