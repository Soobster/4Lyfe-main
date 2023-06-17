package com.cs4530.a4lyfe.models.database.tableBuilder

import com.cs4530.a4lyfe.models.database.tables.UserTable

class UserTableBuilder {
    private var registered: Boolean? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var birthday: String? = null
    private var weight: Double? = null
    private var feet: Double? = null
    private var inches: Double? = null
    private var height: Double? = null
    private var goal: Double? = null
    private var lifestyle: String? = null
    private var sex: String? = null
    private var profileImage: String? = null
    fun setFirstName(firstName: String?): UserTableBuilder {
        this.firstName = firstName
        return this
    }

    fun setLastName(lastName: String?): UserTableBuilder {
        this.lastName = lastName
        return this
    }

    fun setRegistered(registered: Boolean?): UserTableBuilder {
        this.registered = registered
        return this
    }

    fun setBirthday(birthday: String?): UserTableBuilder {
        this.birthday = birthday
        return this
    }

    fun setWeight(weight: Double?): UserTableBuilder {
        this.weight = weight
        return this
    }

    fun setFeet(feet: Double?): UserTableBuilder {
        this.feet = feet
        return this
    }

    fun setInches(inches: Double?): UserTableBuilder {
        this.inches = inches
        return this
    }

    fun setHeight(height: Double?): UserTableBuilder {
        this.height = height
        return this
    }

    fun setGoal(goal: Double?): UserTableBuilder {
        this.goal = goal
        return this
    }

    fun setSex(sex: String?): UserTableBuilder {
        this.sex = sex
        return this
    }

    fun setLifestyle(lifestyle: String?): UserTableBuilder {
        this.lifestyle = lifestyle
        return this
    }

    fun setProfileImage(profileImage: String?): UserTableBuilder {
        this.profileImage = profileImage
        return this
    }

    fun createUserTable(): UserTable {
        return UserTable(
            registered!!,
            lastName!!,
            firstName!!,
            birthday!!,
            weight!!,
            feet!!,
            inches!!,
            height!!,
            goal!!,
            lifestyle!!,
            sex!!,
            profileImage
        )
    }
}