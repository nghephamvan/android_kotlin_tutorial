package com.tma.data.scope

import javax.inject.Scope

@MustBeDocumented
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope
//https://stackoverflow.com/questions/48040243/create-custom-dagger-2-scope-with-kotlin
//Retention annotation will take the default property AnnotationRetention.RUNTIME when you don't pass anything to it.