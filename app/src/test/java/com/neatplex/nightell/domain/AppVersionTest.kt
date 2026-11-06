package com.neatplex.nightell.domain

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.domain.util.AppVersion
import org.junit.Test

class AppVersionTest {

    private fun v(text: String) = AppVersion.parse(text)!!

    @Test
    fun `compares each part as a number`() {
        assertThat(v("1.10.0")).isGreaterThan(v("1.9.0"))
        assertThat(v("2.0.0")).isGreaterThan(v("1.99.99"))
        assertThat(v("1.0.7")).isLessThan(v("1.1.0"))
    }

    @Test
    fun `missing parts count as zero`() {
        assertThat(v("1.1")).isEquivalentAccordingToCompareTo(v("1.1.0"))
        assertThat(v("1.1")).isEqualTo(v("1.1.0"))
        assertThat(v("1.1.1")).isGreaterThan(v("1.1"))
    }

    @Test
    fun `ignores a prefix v and anything after the numbers`() {
        assertThat(v("v1.2.0")).isEqualTo(v("1.2.0"))
        assertThat(v("1.2.0-beta")).isEqualTo(v("1.2.0"))
        assertThat(v(" 1.2.0 (debug)")).isEqualTo(v("1.2.0"))
    }

    @Test
    fun `text without a version does not parse`() {
        assertThat(AppVersion.parse(null)).isNull()
        assertThat(AppVersion.parse("")).isNull()
        assertThat(AppVersion.parse("latest")).isNull()
    }
}
