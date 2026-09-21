package com.wfd.dot1.cwfm.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.wfd.dot1.cwfm.pojo.DeptMapping;
import com.wfd.dot1.cwfm.pojo.WorkmenBulkUpload;
import com.wfd.dot1.cwfm.util.QueryFileWatcher;

@Repository
public class DepartmentMappingDaoImpl implements DepartmentMappingDao{

	@Autowired
    private JdbcTemplate jdbcTemplate;

	 public String existsPEDepMapping() {
		    return QueryFileWatcher.getQuery("GET_EXISTS_PE-DEP-MAPPING");
		}
	 public String getPrincipalEmployerNameById() {
		    return QueryFileWatcher.getQuery("GET_PE_NAME_BY_ID");
		}
	 public String getDepartmentNameById() {
		    return QueryFileWatcher.getQuery("GET_DEPT_NAME_BY_ID");
		}
	 public String getTradeNameById() {
		    return QueryFileWatcher.getQuery("GET_TRADE_NAME_BY_ID");
		}
	 public String saveUnitDeptMaping() {
		    return QueryFileWatcher.getQuery("SAVE_UNIT_DEPT_MAPPING");
		}
	 public String FindAllPeDeptMapping() {
		    return QueryFileWatcher.getQuery("GET_ALL_PE_DEPT_LIST");
		}
	 public String GetAllTradeSkillMappings() {
		    return QueryFileWatcher.getQuery("GET_ALL_PE_TRADE_LIST");
		}
	 public String saveTradeSkillMappings() {
		    return QueryFileWatcher.getQuery("SAVE_TRADE_SKILL_MAPPING");
		}
	 public String existsTradeSkillMapping() {
		    return QueryFileWatcher.getQuery("GET_EXISTS_TRADE_SKILL_MAPPING");
		}
	 
	 @Override
	    public boolean existsMapping(int principalEmployerId, int departmentId) {
		   String sql=existsPEDepMapping();
	        //String sql = "SELECT COUNT(*) FROM UnitDepartmentMapping WHERE principalEmployerId = ? AND departmentId = ?";
	        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, principalEmployerId, departmentId);
	        return count != null && count > 0;
	    }
	 
	 @Override
	    public String getPrincipalEmployerNameById(int id) {
		 String sql=getPrincipalEmployerNameById();
	        //String sql = "SELECT NAME FROM CMSPRINCIPALEMPLOYER WHERE UNITID = ?";
	        return jdbcTemplate.queryForObject(sql, String.class, id);
	    }

	    @Override
	    public String getDepartmentNameById(int id) {
	    	String sql=getDepartmentNameById();
	       // String sql = "SELECT GMNAME FROM CMSGENERALMASTER WHERE GMID = ?";
	        return jdbcTemplate.queryForObject(sql, String.class, id);
	    }
	    
	    @Override
	    public String getTradeNameById(int id) {
	    	String sql=getTradeNameById();
	       // String sql = "SELECT GMNAME FROM CMSGENERALMASTER WHERE GMID = ?";
	        return jdbcTemplate.queryForObject(sql, String.class, id);
	    }
	 
	 @Override
	    public void save(List<DeptMapping> mappings) {
		 String sql=saveUnitDeptMaping();
	       // String sql = "INSERT INTO UnitDepartmentMapping (principalEmployerId, departmentId, subDepartmentId) VALUES (?, ?, ?)";
	        for (DeptMapping mapping : mappings) {
	            jdbcTemplate.update(sql, mapping.getPrincipalEmployerId(), mapping.getDepartmentId(), mapping.getSubDepartmentId());
	        }
	 }
	 public String trioexistsMapping() {
		    return QueryFileWatcher.getQuery("GET_TRIO_MAPPING_EXISTS");
		}
	 @Override
	    public List<DeptMapping> findAll() {
		 List<DeptMapping> peList= new ArrayList<DeptMapping>();
		 String sql=FindAllPeDeptMapping();
		 //String sql="SELECT \r\n"
		 //	+ "   pe.UNITID AS principalEmployerId,\r\n"
		 //		+ "   pe.name AS principalEmployerName,\r\n"
		 //		+ "   d.GMID AS departmentId,\r\n"
		 //		+ "   d.GMNAME AS departmentName,\r\n"
		//		+ "   sd.GMID AS subDepartmentId,\r\n"
		 //		+ "   sd.GMNAME AS subDepartmentName\r\n"
		 //		+ "FROM UnitDepartmentMapping udm\r\n"
		//		+ "JOIN CMSPRINCIPALEMPLOYER pe ON pe.UNITID = udm.principalEmployerId\r\n"
		 //		+ "JOIN CMSGENERALMASTER d ON d.GMID = udm.departmentId\r\n"
		 //		+ "LEFT JOIN CMSGENERALMASTER sd ON sd.GMID = udm.subDepartmentId;";
	      
	        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
	    	while(rs.next()) {
	        	DeptMapping mapping = new DeptMapping();
	           // mapping.setMappingId(rs.getInt("mappingId"));
	            mapping.setPrincipalEmployer(rs.getString("principalEmployerName"));
	            mapping.setDepartment(rs.getString("departmentName"));
	            mapping.setSubDepartment(rs.getString("subDepartmentName"));
	            mapping.setPrincipalEmployerId(rs.getInt("principalEmployerId"));
	            mapping.setDepartmentId(rs.getInt("departmentId"));
	            mapping.setSubDepartmentId(rs.getInt("subDepartmentId"));
	            peList.add(mapping);
	    	}
	    	return peList;
	    	}
	 
	 @Override
	    public List<DeptMapping> findAllTradeSkillMappings() {
		 List<DeptMapping> peList= new ArrayList<DeptMapping>();
		 String sql=GetAllTradeSkillMappings();
	        //String sql = "  SELECT  pe.name AS principalEmployerName,pe.unitid as principalEmployerId,cmsg.GMNAME AS Trade, cmsg.GMID as tradeId, cmsg1.GMNAME AS Skill,cmsg1.GMID as skillId  FROM UnitTradeSkillMapping utsm \r\n"
	        //		+ "	        		        		JOIN CMSPRINCIPALEMPLOYER pe ON utsm.PrincipalEmployerId = pe.UNITID \r\n"
	        //		+ "	        			        		JOIN CMSGENERALMASTER cmsg ON utsm.TradeId = cmsg.GMID \r\n"
	        //	+ "	        		       		JOIN CMSGENERALMASTER cmsg1 ON utsm.SkillId = cmsg1.GMID";
	        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
	    	while(rs.next()) {
	        	DeptMapping mapping = new DeptMapping();
	           // mapping.setMappingId(rs.getInt("mappingId"));
	            mapping.setPrincipalEmployer(rs.getString("PrincipalEmployerName"));
	            mapping.setTrade(rs.getString("Trade"));
	            mapping.setSkill(rs.getString("Skill"));
	            mapping.setPrincipalEmployerId(rs.getInt("principalEmployerId"));
	            mapping.setTradeId(rs.getInt("tradeId"));
	            mapping.setSkillId(rs.getInt("skillId"));
	            mapping.setWorkmenCount(rs.getString("WorkmenCount"));
	            peList.add(mapping);
	    	}
	    	return peList;
	    	}
	 
	 @Override
	    public void saveTradeSkillMappings(List<DeptMapping> mappings) {
		 String sql=saveTradeSkillMappings();
	        //String sql = "INSERT INTO UnitTradeSkillMapping(principalEmployerId, tradeid, skillid) VALUES (?, ?, ?)";
	        for (DeptMapping mapping : mappings) {
	            jdbcTemplate.update(sql, mapping.getPrincipalEmployerId(), mapping.getTradeId(), mapping.getSkillId(),mapping.getWorkmenCount());
	        }
	 }

	@Override
	public boolean existsTradeSkillMapping(int principalEmployerId, int tradeId, int skillId) {
		String sql=existsTradeSkillMapping();
		  //String sql = "SELECT COUNT(*) FROM UnitTradeSkillMapping WHERE PrincipalEmployerId = ? AND TradeId = ? AND SkillId = ?";
	        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, principalEmployerId, tradeId,skillId);
	        return count != null && count > 0;
	    }
	@Override
	public String getSubDepartmentNameById(int subDepartmentId) {
		String sql=getDepartmentNameById();
		 //String sql = "SELECT GMNAME FROM CMSGENERALMASTER WHERE GMID = ?";
        return jdbcTemplate.queryForObject(sql, String.class, subDepartmentId);
    }
	@Override
	public boolean trioexistsMapping(int principalEmployerId, int departmentId, int subDepartmentId) {
		String sql=trioexistsMapping();
		//String sql = "SELECT COUNT(*) FROM UnitDepartmentMapping WHERE principalEmployerId = ? AND departmentId = ? and subDepartmentId=?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, principalEmployerId, departmentId,subDepartmentId);
        return count != null && count > 0;
	}
	 public String checkDependencyinGatepass() {
		    return QueryFileWatcher.getQuery("CHECK_UNITMAPPING_DEPENDECY_IN_GATEPASS");
		}
	@Override
    public boolean checkDependency(int principalEmployerId) {
		String sql=checkDependencyinGatepass();
        //String sql = "SELECT COUNT(*) FROM UnitDepartmentMapping udm JOIN GATEPASSMAIN g ON g.UnitId=udm.principalEmployerId and g.departmentId = udm.departmentId AND (g.AreaId = udm.subDepartmentId OR udm.subDepartmentId IS NULL) WHERE udm.principalEmployerId = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, principalEmployerId);
        return count != null && count > 0;
    }
	public String deleteMappings() {
	    return QueryFileWatcher.getQuery("DELETE_UNIT_DEPARTMENT_MAPPING");
	}
    @Override
    public int deleteMappings(int principalEmployerId) {
    	String sql=deleteMappings();
        //String sql = "DELETE FROM UnitDepartmentMapping WHERE principalEmployerId = ?";
        return jdbcTemplate.update(sql, principalEmployerId);
    }
	@Override
	public void delete(int principalEmployerId, int departmentId, int subDepartmentId) {
		// TODO Auto-generated method stub
		
	}
	public String DeptexistsInGatePass() {
	    return QueryFileWatcher.getQuery("DEPARTMENT_EXISTS_IN_GATEPASS");
	}
	 @Override
	    public boolean DeptexistsInGatePass(DeptMapping mapping) {
		 String sql=DeptexistsInGatePass();
	        //String sql = "SELECT COUNT(*) FROM GATEPASSMAIN g WHERE g.UnitId = ? AND g.DepartmentId = ? AND (g.AreaId = ? OR (g.AreaId IS NULL AND ? = 0))";
	        Integer count = jdbcTemplate.queryForObject(sql, Integer.class,
	                mapping.getPrincipalEmployerId(),
	                mapping.getDepartmentId(),
	                mapping.getSubDepartmentId(),
	                mapping.getSubDepartmentId());
	        return count != null && count > 0;
	    }
	 public String deletePEDeptSubDeptMapping() {
		    return QueryFileWatcher.getQuery("DELETE_PE_DEPT_SUBDEPT_MAPPING");
		}
	    @Override
	    public void deleteDeptMapping(DeptMapping mapping) {
	    	String sql=deletePEDeptSubDeptMapping();
	       // String sql = "DELETE FROM UnitDepartmentMapping " +
	       //              "WHERE PRINCIPALEMPLOYERID = ? AND DEPARTMENTID = ? AND SUBDEPARTMENTID = ?";
	        jdbcTemplate.update(sql,
	                mapping.getPrincipalEmployerId(),
	                mapping.getDepartmentId(),
	                mapping.getSubDepartmentId());
	    }
	    public String TradeexistsInGatePass() {
		    return QueryFileWatcher.getQuery("TRADE_EXISTS_IN_GATEPASS");
		}
	    public String deleteTradeMapping() {
		    return QueryFileWatcher.getQuery("DELETE_TRADE_MAPPING");
		}
	    @Override
	    public boolean TradeexistsInGatePass(DeptMapping mapping) {
	    	String sql=TradeexistsInGatePass();
	       // String sql = "SELECT COUNT(*) FROM GATEPASSMAIN g WHERE g.UnitId = ? AND g.TRADEID = ? AND g.SKILLID = ?";
	        Integer count = jdbcTemplate.queryForObject(sql, Integer.class,
	                mapping.getPrincipalEmployerId(),
	                mapping.getTradeId(),
	                mapping.getSkillId());
	        return count != null && count > 0;
	    }

	    @Override
	    public void deleteTradeMapping(DeptMapping mapping) {
	    	String sql=deleteTradeMapping();
	       // String sql = "DELETE FROM UnitTradeSkillMapping " +
	      //               "WHERE PRINCIPALEMPLOYERID = ? AND TRADEID = ? AND SKILLID = ?";
	        jdbcTemplate.update(sql,
	                mapping.getPrincipalEmployerId(),
	                mapping.getTradeId(),
	                mapping.getSkillId());
	    }
		@Override
		public boolean plantDepartmentMappingExists(Integer unitId, Integer departmentId) {
			//String sql=trioexistsMapping();
			String sql = "SELECT COUNT(*) FROM UnitDepartmentMapping WHERE principalEmployerId = ? AND departmentId = ? ";
	        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, unitId, departmentId);
	        return count != null && count > 0;
		}
	}
	
	
