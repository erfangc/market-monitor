package com.github.erfangc.marketmonitor.analysis

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CrossSectionalValuationController(
        private val crossSectionalValuationService: CrossSectionalValuationService
) {
    @GetMapping
    fun crossSectionals(): List<CrossSectional> {
        return crossSectionalValuationService.crossSectionals()
    }
}
