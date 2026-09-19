# Galactic Armillary UI correction — 2026-09-14

The user explicitly requested the attached Fornax-style JEI UI and a corrected startup rocket, using D:/tmp/GregECore as source. This is a correction to the existing approved machine, not a new port.

Source and deployed PNG both have SHA-256 `2a326b9d685fa09ba0c700aaaf58ad0f6d6e0d6372db6d0e2ca813ed3e63384d` (1518 bytes). Source: `D:/tmp/GregECore/src/main/resources/assets/gregecore/textures/progress/fornax.png`. Actual dimensions are 90x98, two 90x49 frames. The previous contract's 86x32 frame claim was incorrect. No PNG bytes were changed.

GTCEu 7.4.0's local mapped sources show GTRecipeTypeUI's default 164x62 content area for nine inputs and one output; GTRecipeWidget adds 35 pixels for native recipe text. Its appendJEIUI callback runs after content binding and again when the displayed voltage changes. Preserve these native dimensions and bindings. Position the progress image at (49,0), size 98x62, and the output parent at (140,18) to align the source branches, input grid and centered output with the user reference.

The controller draws the 19x18 original rocket at native resolution from (43,66). Five rectangular strips exclude the branching arrow crossing rows 73–75 without modifying the source image. Travel is bounded inside the existing 99-pixel segmented bar; the percentage remains separate. Both energy and warm-up use this shared widget.

Validation: full Gradle build passed with 248 tests, including the atlas dimensions, arrow exclusion, complete rocket color coverage and source-to-destination offset regression check. Machine contract, casing catalog, locked source and production textures, formed appearance, localization and git diff whitespace checks passed. Development client startup smoke run launched. In-world and interactive JEI visual verification remain pending because native-app interaction is unavailable in this session.
