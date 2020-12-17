package os.dtakac.feritraspored.common.di

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import os.dtakac.feritraspored.common.assets.AssetProvider
import os.dtakac.feritraspored.common.assets.AssetProviderImpl
import os.dtakac.feritraspored.common.network.NetworkChecker
import os.dtakac.feritraspored.common.network.NetworkCheckerImpl
import os.dtakac.feritraspored.common.preferences.PreferenceRepository
import os.dtakac.feritraspored.common.preferences.PreferenceRepositoryImpl

val appModule = module {
    single<AssetProvider> { AssetProviderImpl(androidContext()) }
    single<NetworkChecker> { NetworkCheckerImpl(androidContext()) }
    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(get()) }
    single<PreferenceRepository> { PreferenceRepositoryImpl(get()) }
}