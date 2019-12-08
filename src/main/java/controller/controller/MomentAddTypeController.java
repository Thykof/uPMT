/*****************************************************************************
 * MomentAddTypeController.java
 *****************************************************************************
 * Copyright © 2017 uPMT
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

package controller.controller;

import java.util.LinkedList;

import model.Category;
import model.MomentExperience;
import model.Type;

public class MomentAddTypeController implements controller.controller.Observable{

	private MomentExperience momentExp;
	private LinkedList<Observer> ObsMomentNames;
	
	public MomentAddTypeController(MomentExperience moment) {
		this.momentExp = moment;
		ObsMomentNames = new LinkedList<Observer>();
	}

	@Override
	public void update(Object value) {
		
		if(value != null) {
			momentExp.getCategories().add((Category) value);
		}else {
			try {
			momentExp.getCategories().removeLast();
			}
			catch(Exception e) {}
		}
		for(Observer obs : ObsMomentNames) {
			obs.updateVue(this, value);
		}
	}

	@Override
	public void addObserver(Observer o) {
		ObsMomentNames.add(o);
	}

	@Override
	public void updateModel(Object value) {
		this.momentExp = (MomentExperience) value;
	}
	
	@Override
	public void RemoveObserver(Observer o) {
		ObsMomentNames.remove(o);
	}
}