package com.cs4530.a4lyfe.models.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserTable(
    private var registered: Boolean,
    @PrimaryKey
    private var lastName: String,
    private var firstName: String,
    private var birthday: String,
    private var weight: Double,
    private var feet: Double,
    private var inches: Double,
    private var height: Double,
    private var goal: Double,
    private var lifestyle: String,
    private var sex: String,
    private var profileImage: String?,
) {

    fun getLastName(): String {
        return lastName
    }

    fun setLastName(lastName: String) {
        this.lastName = lastName
    }

    fun getRegistered(): Boolean {
        return registered
    }

    fun setRegistered(registered: Boolean) {
        this.registered = registered
    }

    fun getFirstName(): String {
        return firstName
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun getBirthday(): String {
        return birthday
    }

    fun setBirthday(birthday: String) {
        this.birthday = birthday
    }

    fun getWeight(): Double {
        return weight
    }

    fun setWeight(weight: Double) {
        this.weight = weight
    }

    fun getInches(): Double {
        return inches
    }

    fun setInches(inches: Double) {
        this.inches = inches
    }

    fun getFeet(): Double {
        return feet
    }

    fun setFeet(feet: Double) {
        this.feet = feet
    }

    fun getHeight(): Double {
        return height
    }

    fun setHeight(height: Double) {
        this.height = height
    }

    fun getGoal(): Double {
        return goal
    }

    fun setGoal(goal: Double) {
        this.goal = goal
    }

    fun getLifestyle(): String {
        return lifestyle
    }

    fun setLifestyle(lifestyle: String) {
        this.lifestyle = lifestyle
    }

    fun getSex(): String {
        return sex
    }

    fun setSex(sex: String) {
        this.sex = sex
    }

    /*This will have to be converted to bitmap*/
    fun getProfileImage(): String? {
        //return ModelsKt.encodeBitMapFromString(profileImage);
        return profileImage
    }

    fun setProfileImage(profileImage: String?) {
        this.profileImage = profileImage
        //this.profileImage = ModelsKt.encodeBitMap( profileImage);
    }
}