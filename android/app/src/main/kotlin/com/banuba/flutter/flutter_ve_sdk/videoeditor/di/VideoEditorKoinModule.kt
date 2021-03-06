package com.banuba.flutter.flutter_ve_sdk.videoeditor.di

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.banuba.flutter.flutter_ve_sdk.R
import com.banuba.flutter.flutter_ve_sdk.videoeditor.data.TimeEffects
import com.banuba.flutter.flutter_ve_sdk.videoeditor.data.VisualEffects
import com.banuba.flutter.flutter_ve_sdk.videoeditor.export.IntegrationAppExportFlowManager
import com.banuba.flutter.flutter_ve_sdk.videoeditor.export.IntegrationAppExportParamsProvider
import com.banuba.flutter.flutter_ve_sdk.videoeditor.export.IntegrationAppExportResultHandler
import com.banuba.flutter.flutter_ve_sdk.videoeditor.impl.IntegrationAppRecordingAnimationProvider
import com.banuba.flutter.flutter_ve_sdk.videoeditor.impl.IntegrationAppWatermarkProvider
import com.banuba.flutter.flutter_ve_sdk.videoeditor.impl.IntegrationTimerStateProvider
import com.banuba.sdk.cameraui.data.CameraRecordingAnimationProvider
import com.banuba.sdk.cameraui.data.CameraTimerStateProvider
import com.banuba.sdk.core.AREffectPlayerProvider
import com.banuba.sdk.core.IUtilityManager
import com.banuba.sdk.effectplayer.adapter.BanubaAREffectPlayerProvider
import com.banuba.sdk.effectplayer.adapter.BanubaClassFactory
import com.banuba.sdk.ve.effects.EditorEffects
import com.banuba.sdk.ve.effects.WatermarkProvider
import com.banuba.sdk.ve.flow.ExportFlowManager
import com.banuba.sdk.ve.flow.ExportResultHandler
import com.banuba.sdk.ve.flow.FlowEditorModule
import com.banuba.sdk.veui.data.ExportParamsProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.definition.BeanDefinition
import org.koin.core.qualifier.named

/**
 * All dependencies mentioned in this module will override default
 * implementations provided from SDK.
 * Some dependencies has no default implementations. It means that
 * these classes fully depends on your requirements
 */
class VideoEditorKoinModule : FlowEditorModule() {

    override val effectPlayerManager: BeanDefinition<AREffectPlayerProvider> =
        single(override = true) {
            BanubaAREffectPlayerProvider(
                mediaSizeProvider = get(),
                token = androidContext().getString(R.string.video_editor_token)
            )
        }

    override val utilityManager: BeanDefinition<IUtilityManager> = single(override = true) {
        BanubaClassFactory.createUtilityManager(
            context = get()
        )
    }

    override val exportFlowManager: BeanDefinition<ExportFlowManager> = single {
        IntegrationAppExportFlowManager(
            exportDataProvider = get(),
            editorSessionHelper = get(),
            exportDir = get(named("exportDir")),
            mediaFileNameHelper = get()
        )
    }

    override val exportResultHandler: BeanDefinition<ExportResultHandler> = single {
        IntegrationAppExportResultHandler()
    }

    /**
     * Provides params for export
     * */
    override val exportParamsProvider: BeanDefinition<ExportParamsProvider> =
        factory(override = true) {
            IntegrationAppExportParamsProvider(
                exportDir = get(named("exportDir")),
                sizeProvider = get(),
                watermarkBuilder = get()
            )
        }

    /**
     * Provides path for exported files
     * */
    override val exportDir: BeanDefinition<Uri> = single(named("exportDir"), override = true) {
        get<Context>().getExternalFilesDir("")
            ?.toUri()
            ?.buildUpon()
            ?.appendPath("export")
            ?.build() ?: throw NullPointerException("exportDir should't be null!")
    }

    override val watermarkProvider: BeanDefinition<WatermarkProvider> = factory(override = true) {
        IntegrationAppWatermarkProvider()
    }

    override val editorEffects: BeanDefinition<EditorEffects> = single(override = true) {
        val visualEffects = listOf(
            VisualEffects.VHS,
            VisualEffects.Rave
        )
        val timeEffects = listOf(
            TimeEffects.SlowMo(),
            TimeEffects.Rapid()
        )

        EditorEffects(
            visual = visualEffects,
            time = timeEffects,
            equalizer = emptyList()
        )
    }

    /**
     * Provides camera record button animation
     * */
    override val cameraRecordingAnimationProvider: BeanDefinition<CameraRecordingAnimationProvider> =
        factory(override = true) {
            IntegrationAppRecordingAnimationProvider(context = get())
        }

    override val cameraTimerStateProvider: BeanDefinition<CameraTimerStateProvider> =
            factory {
                IntegrationTimerStateProvider()
            }
}