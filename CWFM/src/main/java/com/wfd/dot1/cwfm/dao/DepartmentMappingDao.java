package com.wfd.dot1.cwfm.dao;

import java.util.List;

import com.wfd.dot1.cwfm.pojo.DeptMapping;

public interface DepartmentMappingDao {

	void save(List<DeptMapping> mappings);
	
	List<DeptMapping> findAll();

	boolean existsMapping(int principalEmployerId, int departmentId);
	
	  String getPrincipalEmployerNameById(int id);
	  
	    String getDepartmentNameById(int id);

		List<DeptMapping> findAllTradeSkillMappings();

		String getTradeNameById(int tradeId);

		void saveTradeSkillMappings(List<DeptMapping> mappings);

		boolean existsTradeSkillMapping(int principalEmployerId, int tradeId, int skillId);

		String getSubDepartmentNameById(int subDepartmentId);
		
		boolean trioexistsMapping(int principalEmployerId, int departmentId, int subDepartmentId);

		boolean checkDependency(int principalEmployerId);
		
	    int deleteMappings(int principalEmployerId);

		void delete(int principalEmployerId, int departmentId, int subDepartmentId);
		
		 boolean DeptexistsInGatePass(DeptMapping mapping);
		 
		    void deleteDeptMapping(DeptMapping mapping);

			boolean TradeexistsInGatePass(DeptMapping mapping);

			void deleteTradeMapping(DeptMapping mapping);

			boolean plantDepartmentMappingExists(Integer unitId, Integer departmentId);
}
