# Galactic Armillary approved-design evidence

## Identity and scope

- Display name: Galactic Armillary
- Registry ID: `tstmodern:galactic_armillary`
- Authority: user-approved original design derived from the generated Galactic Armillary concept image.
- Approved implementation scope: UEV Infinity production, one configurable-time warm-up plus fixed `2,560,000,000 EU` charge per uninterrupted operating session, fixed recipe duration/parallel, the 16 top-face energy sockets, activation-gated galaxy visibility, controller progress bars, and the startup beam/ignition burst.

## Structure

- Dimensions: 41 width x 39 height x 41 depth.
- Design axes: X left-to-right, Y bottom-to-top, Z front-to-back.
- GTCEu axes: RIGHT = +X, DOWN = -Y, BACK = +Z.
- Controller: `(20, 3, 20)`, directly above the center of the new 5x5 foundation-top service pad, with horizontal facing.
- Center: `(20, 18, 20)`.
- Foundation: three-level circular black platform with cyan radial and circular inlays. Its exact center is a 5x5 service pad: a 3x3 High Power center surrounded by a 16-block Astral Pylon perimeter.
- Supports: four inward-curving black diagonal arches with cyan joints and purple field nodes.
- Armillary: two cyan radius-9 rings centered at `(20, 18, 20)`, tilted symmetrically by +30 and -30 degrees from the horizontal XZ plane and stretched 1.5x on Y. Their horizontal span and two purple intersections on the front/back Z axis remain fixed, while the taller silhouette makes the central crossing clearer from the front.
- Crown: stacked black suspension rings with cyan accents and eight purple nodes.
- Satellite shell: a required physical assembly centered at `(20, 37, 20)`, one block below the previous approved placement. It has one horizontal radius-9 Extreme Density outer ring, four identical cardinal wings with Extreme Density frames and High Power center strips, two opposite Astral Pylon capacitor nodes on the X-axis, and a three-layer 7x7 open square housing around the reserved core cavity. A downward cannon uses three hollow rounded rings at each diameter: 7x7 at Y=35..33, 5x5 at Y=32..30, and High Power 3x3 at Y=29..27. The final two 3x3 rings at Y=28/27 retain only their four corners. Cardinal connector blocks join adjacent sizes without closing the central beam path. Every center cell from Y=35 through Y=27 remains open so the plasma beam can pass through toward the machine core.
- Renderer boundary: the user-authored galaxy renderer is visible only after the multiblock is formed and the internal core has completed activation; placing or merely forming the controller must not render the galaxy.

## Approved UEV energy and continuous-operation contract

- A single satellite is fixed on the machine's vertical symmetry axis above the core. Its bilaterally symmetric body avoids splitting one satellite into unrelated halves and fires a vertical plasma beam at the core.
- The machine is UEV and accepts 1-16 normal Energy Input Hatches, each UEV or higher, in the 16 dedicated top-face sockets. Empty sockets remain High Power Casing.
- The first valid recipe of a cold operating session is reserved but not consumed while the machine drains hatch energy into a fixed internal `2,560,000,000 EU` core buffer and advances the configured warm-up timer. Hatch count/tier changes only the energy-charge component.
- The default warm-up is `120` seconds and is configurable through `galacticArmillaryStartupSeconds` (20-3600 seconds). The server permits the reserved recipe only after both the timer and fixed charge threshold are complete. While charging, the satellite core glows, fires a broad layered vertical plasma beam through the open cannon, and the target core expands according to the slower of energy/time progress. Completion continues the expansion, produces a 30-tick harmless visual ignition burst, and only then reveals the galaxy. These effects are render-only and do not consume inputs or alter the world.
- The expanding target core follows the locally audited Draconic Evolution principle: reactor size is a continuously scaled render value and beam width grows toward the core. Authority: `D:\tmp\DraconicEvolution\src\main\java\com\brandon3055\draconicevolution\client\render\tile\RenderTileReactorCore.java` (SHA-256 `f45278e953e5b5ac4e0f91fbb77a1a51d062e0440c4a8507e55d2e1712b4eff1`). No source code, shader, model, or texture is copied.
- During startup only, the controller shows separate energy and warm-up lines, each followed by 20 compact 4x4 square segments, a numeric percentage, and the GregEcore Fornax rocket sprite moving horizontally with progress. The full controller screen keeps the normal dark GTCEu display background; startup widgets disappear once processing starts so recipe progress remains unobstructed.
- Once `STABLE`, consecutive valid recipes run without repeating warm-up. A normal boundary between recipes is not an interruption.
- Loss of structure validity, required energy/input/output capacity, or an explicit stop ends the hot session, hides the galaxy, and requires the full warm-up before processing resumes.
- Charge/session state is server-authoritative, synchronized to clients, and persisted across chunk unload and server restart rather than being advanced by renderer state.
- Recipe voltage is fixed at `VA[UEV] = 7,864,320 EU/t`; overclock is disabled and parallel is always `1`.

## Approved existing casing palette

The user delegated casing selection to the implementation and approved choosing the best existing blocks for the concept palette.

| Symbol | Count | Existing target | Visual role |
|---|---:|---|---|
| `~` | 1 | `tstmodern:galactic_armillary` | controller |
| `D` | 5358 | `tstmodern:extreme_density_casing` | black foundation, outer arches, crown, satellite frame/ring and tapered cannon rings |
| `H` | 291 | `tstmodern:high_power_casing` | cyan platform/crown inlays, 3x3 center service pad, satellite wing centers and final three 3x3 cannon rings; exactly one item import and one item export may replace H positions |
| `E` | 16 | `tstmodern:astral_pylon_casing` or UEV+ Energy Input Hatch | 5x5 service-pad perimeter at local Y=2; these are the only legal energy positions, global minimum 1 and maximum 16 |
| `I` | 112 | `tstmodern:high_power_casing` | +30-degree, 1.5x-Y armillary ring |
| `O` | 112 | `tstmodern:high_power_casing` | -30-degree, 1.5x-Y armillary ring |
| `A` | 72 | `tstmodern:astral_pylon_casing` | purple field/ring-intersection nodes and two satellite capacitors |

No casing is newly registered, copied, recolored, or synthesized.

## Recipe pool

- Diamond Lattice: shaped crafting, Diamond Blocks at all four corners, Emerald Blocks at all four edge centers, Nether Star in the center, output 1 Diamond Lattice.
- Crystal Matrix Ingot: EBF, 64 Diamond Lattice + 16,000 mB UU Matter -> 16 Crystal Matrix Ingots, 9001 K minimum, `VA[ZPM]`, 400 ticks.
- Infinity Catalyst Nugget: Galactic Armillary, non-consumable Programmed Circuit configuration 1 and no material/fluid input -> 1 Infinity Catalyst Nugget, `VA[UEV]`, 100 ticks.
- Infinity Catalyst: shaped 3x3 crafting from 9 Infinity Catalyst Nuggets -> 1 Infinity Catalyst.
- Infinity Ingot: Galactic Armillary, 4 Infinity Catalysts + 16 Neutronium Blocks + 32 Crystal Matrix Ingots -> 1 Infinity Ingot, `VA[UEV]`, 200 ticks.
- When `avaritia` is loaded and its registered item exists, Diamond Lattice, Crystal Matrix Ingot, Infinity Catalyst, and Infinity Ingot recipes resolve to `avaritia:*`; otherwise Infinity Ingot resolves to the TST Modern `Infinity` material and the other ingredients use their internal equivalents. The optional `forge:ingots/infinity` entry lets the external ingot enter the same GTCEu processing recipes. Infinity Catalyst Nugget remains internal because Re-Avaritia has no corresponding nugget item.

## Texture authority

- Controller presentation exactly reuses the Fornax Universi controller model recipe from the audited local source: native GTCEu GCYM High Temperature Smelting Casing plus the native GTCEu Fusion Reactor controller overlay. The source controller is `D:\tmp\GregEcore\src\main\java\net\cu5tmtp\GregECore\gregstuff\GregMachines\machines\endgame\FornaxUniversi.java` (SHA-256 `177fdb2f33b452d03ae7d2744e5132f0b1f0f0542713d108b1b2da7c86b1d85e`) and its generated model is `D:\tmp\GregEcore\src\generated\resources\assets\gregecore\models\block\machine\fornaxuniversi.json` (SHA-256 `b2ddd0f6b1ed3adf7639fe09d1a4a6255f27b0c98d8edb25f19afd06d01bf365`).
- JEI uses GregEcore's actual `9` item-input / `1` item-output layout plus the byte-identical Fornax progress texture from `D:\tmp\GregEcore\src\main\resources\assets\gregecore\textures\progress\fornax.png`. Because GTCEu 7.4 otherwise forces every progress texture into 20x20, this map expands its JEI canvas and renders the progress frame at its exact source size of 86x32; the controller reuses its rocket sprite (SHA-256 `2a326b9d685fa09ba0c700aaaf58ad0f6d6e0d6372db6d0e2ca813ed3e63384d`).
- Formed appearance and structural black role reuse the existing locked Extreme Density casing.
- Cyan role reuses the existing locked High Power casing.
- Purple role reuses the shared Astral Pylon casing, updated to the user-supplied animated `cosmic_glass` side and `cosmic_glass_pane_top` end textures with interpolation metadata from `C:\Users\mtien\Downloads\files.zip`.
- Diamond Lattice, Crystal Matrix Ingot, Infinity Catalyst, and Infinity Ingot retain their byte-identical Avaritia PNGs. The fallback Infinity material uses byte-identical GT5 `CUSTOM/infinity` PNGs only for missing forms: hot ingot, nugget, dusts, plates, rods, fasteners, rings, springs, foil, fine wire, gears, rotor, block, and frame. Client tinting adds the approved four-second rainbow cycle to generated Infinity material items except the ingot, which keeps Avaritia's original animated appearance; the placed block, frame, and pipe textures use the same four-second period through native sprite metadata without forcing chunk rebuilds. GT5's verified Infinity fluid pipe values are preserved as `10,000,000 K` and `60,000 mB/t`; the generated forms include molten/plasma, blocks, frames, and tiny through huge fluid pipes. No cable property is invented because the audited source does not provide electrical ratings.
- Fusion Glass, the four former inner glass pillars, and the small horizontal cyan ring are removed. The four large outer support arches remain unchanged.
- Five production item PNGs are added as byte-identical source copies, with their original animation metadata where present.

## Approval references

- User approved the Galactic Armillary name, registry ID, 41 x 37 x 41 prototype size, and structure-first/render-last implementation.
- User explicitly delegated selection of existing codebase casings that best match the approved black/cyan/purple concept palette.
- User required two separated inner rings for future independent animation, removal of only the four inner Fusion Glass pillars, relocation of the controller onto layer four above the three-layer bottom center, and exact Fornax Universi controller visuals.
- User subsequently required removal of the small horizontal cyan ring and a clickable controller.
- User subsequently required both armillary rings to cross diagonally along the horizontal axis so their shared node is visible from the front.
- User subsequently required the crossed rings to extend farther vertically without changing their horizontal span or intersections.
- User subsequently required the galaxy renderer to remain hidden until the multiblock forms.
- User approved the Draconic Reactor-inspired expanding-core warm-up, a fixed top-center satellite, a 120-second one-time warm-up per uninterrupted recipe chain, a harmless visual ignition burst, and full warm-up again after a genuine interruption.
- User explicitly confirmed that the `cosmic_glass` asset in `files.zip` replaces the Astral Pylon casing texture.
- User explicitly requested using `pane_top` and replacing the former `cube_all` model with distinct side and top/bottom textures.
- User approved hardcoding the satellite shell into the total structure for an in-game preview before implementing its core, beam, illumination, or operating animation.
- User then required raising the satellite body two to three blocks above the crown and adding a cannon-shaped tail that narrows toward the machine core.
- User then required lowering the satellite by one block and replacing the cannon with consecutive hollow 7x7, 5x5, and 3x3 rings, leaving the final center open for the beam.
- User then required three consecutive rings of each diameter, producing a hollow 7x7 x3, 5x5 x3, and 3x3 x3 cannon while preserving the open beam axis.
- User then required removing the side-center blocks from the final two 3x3 rings, leaving only their four corners.
- User approved Galactic Armillary as a UEV Infinity machine with a configurable `120`-second default warm-up, fixed `2,560,000,000 EU` startup buffer, 1-16 UEV+ Energy Input Hatches affecting charge time only, no overclock, parallel 1, 5-second Catalyst Nugget and 10-second Infinity Ingot recipes.
- User superseded the former outer top-surface energy sockets: the only 16 energy positions are now the Astral Pylon perimeter of the centered 5x5 service pad at local Y=2, with the controller directly above its center.
- User clarified that Infinity Catalyst Nugget consumes no material or fluid: Programmed Circuit configuration 1 selects the 5-second energy-only recipe.
- User rejected generated substitute icons and required the exact original Infinity-related textures from the local source repositories.
- User requested long percentage bars below both controller startup lines and required the previously deferred satellite beam and ignition explosion to be visible.
- User required those bars to remain square-segmented but narrower horizontally, requested the GregEcore Fornax JEI presentation, and required its rocket to move with each controller percentage.
