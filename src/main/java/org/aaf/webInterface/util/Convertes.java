/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 
package org.aaf.webInterface.util;

import org.aaf.dto.MemberDTO;
import org.escola.model.Member;

public class Convertes {

	public static Member getUser(MemberDTO dto) {
		if (dto != null) {
			Member userFM = new Member();
			userFM.setId(dto.getId());
			userFM.setLogin(dto.getLogin());
			userFM.setName(dto.getName());
			userFM.setSenha(dto.getSenha());
			return userFM;

		} else {
			return null;
		}
	}

	public static MemberDTO getUser(Member user) {
		if (user != null) {
			MemberDTO userDTO = new MemberDTO();
			userDTO.setEmail(user.getEmail());
			userDTO.setId(user.getId());
			userDTO.setLogin(user.getLogin());
			userDTO.setName(user.getName());
			userDTO.setSenha(user.getSenha());
			return userDTO;

		} else {
			return null;
		}
	}

	
}
*/