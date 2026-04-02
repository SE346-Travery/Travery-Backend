package com.travery.traverybackend.entities.user;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "admins")
@SuperBuilder
@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
public class Admin extends User {
}
