package com.deepmindslab.movenet.visbility

import com.deepmindslab.movenet.data.Person

class BodyPartsVisibilityUtils {

    companion object{
        fun jointVisibility(person:Person,jointNum:Int):Boolean{
            if(person.keyPoints[jointNum].score>0.25){
                return true
            }
            return false
        }
    }

}