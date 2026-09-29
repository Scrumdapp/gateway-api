package com.scrumdapp.gateway.passports

import org.springframework.data.repository.CrudRepository


interface PassportRepository: CrudRepository<Passport, Long> {
}