/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xmd.technician.chat;

import com.hyphenate.chat.EMContact;

public class ChatUser extends EMContact {

    /**
     * 昵称首字母
     */
	protected String initialLetter;
	/**
	 * 用户头像
	 */
	private String avatar;

	public ChatUser(String username){
		if(username == null){
			throw new RuntimeException("username is null");
		}
	    this.username = username;
	}

	public String getInitialLetter() {
		return initialLetter;
	}

	public void setInitialLetter(String initialLetter) {
		this.initialLetter = initialLetter;
	}


	public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
	public int hashCode() {
		return 17 * getUsername().hashCode();
	}

	/**
	 * 用户名相同即为同一用户
	 * @param o
	 * @return
     */
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ChatUser)) {
			return false;
		}
		return getUsername().equals(((ChatUser) o).getUsername()) ;
	}

	/**
	 * 用户所有信息都一致才为同一用户
	 * @param o
	 * @return
     */
	public boolean exactlyEquals(Object o) {
		boolean result = false;
		if (o == null || !(o instanceof ChatUser)) {
			return false;
		}

		if(getAvatar() == null ){
			if(((ChatUser) o).getAvatar()!= null){
				return false;
			}else {
				result = getUsername().equals(((ChatUser) o).getUsername()) && getNick().equals(((ChatUser) o).getNick());
			}
		}else {
			result = getUsername().equals(((ChatUser) o).getUsername()) && getNick().equals(((ChatUser) o).getNick()) && getAvatar().equals(((ChatUser) o).getAvatar());
		}

		return result;
	}

	@Override
	public String toString() {
		return nick == null ? username : nick;
	}
}
