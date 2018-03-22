/*****************************************************************************
 * Classe.java
 *****************************************************************************
 * Copyright � 2017 uPMT
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

public class Category extends Type implements Serializable, Cloneable {

	public Category(String nom) {
		super(nom);
	}
	
	public Category clone(){
		Category newc = new Category(this.getName());
		newc.setColor(this.getColor());
		for(Type t : this.getTypes()){
			newc.addType(new Property(t.getName()));
		}
		return newc;
	}
	
	@Override
	public boolean equals(Object o){
		if(!o.getClass().equals(this.getClass())) {
			return false;
		}
		Category tmp = (Category)o;
		if(tmp != null){
			return tmp.mName.equals(this.mName) &&
					tmp.mDescription.equals(this.mDescription) &&
					tmp.getTypes().equals(this.getTypes());
		}else{
			return false;
		}		
	}

}