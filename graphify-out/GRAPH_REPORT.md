# Graph Report - TST-Modern  (2026-09-18)

## Corpus Check
- Large corpus: 848 files · ~102,214 words. Semantic extraction will be expensive (many Claude tokens). Consider running on a subfolder.

## Summary
- 1749 nodes · 5315 edges · 75 communities (52 shown, 23 thin omitted)
- Extraction: 99% EXTRACTED · 1% INFERRED · 0% AMBIGUOUS · INFERRED: 57 edges (avg confidence: 0.81)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- Machine Recipe Systems (0)
- Draconic Crucible Multiblock (1)
- Machine Recipe Systems (2)
- Machine Recipe Systems (3)
- Machine Recipe Systems (4)
- Astral Computing Array (5)
- Machine Recipe Systems (6)
- Machine Recipe Systems (7)
- Machine Recipe Systems (8)
- Machine Recipe Systems (9)
- Incompact Cyclotron Multiblock (10)
- Naquadah Reactor System (11)
- Starcore Miner Multiblock (12)
- Disassembler Multiblock System (13)
- Disassembler Multiblock System (14)
- Galactic Armillary Multiblock (15)
- Disassembler Multiblock System (16)
- Incompact Cyclotron Multiblock (17)
- Block Registry & Casings (18)
- Incompact Cyclotron Multiblock (19)
- Galactic Armillary Multiblock (20)
- Draconic Crucible Multiblock (21)
- Incompact Cyclotron Multiblock (22)
- Disassembler Multiblock System (23)
- Item Registry & Components
- Big Bro Array Multiblock (25)
- Disassembler Multiblock System (26)
- Big Bro Array Multiblock (27)
- Starcore Miner Multiblock (28)
- Big Bro Array Multiblock (29)
- Big Bro Array Multiblock (30)
- Big Bro Array Multiblock (31)
- Material Registry & Elements
- Ore Processing Factory (33)
- Naquadah Reactor System (34)
- Naquadah Reactor System (35)
- Big Bro Array Multiblock (36)
- Disassembler Multiblock System (37)
- Big Bro Array Multiblock (38)
- Galactic Armillary Multiblock (39)
- Big Bro Array Multiblock (40)
- Disassembler Multiblock System (41)
- Disassembler Multiblock System (42)
- Ore Processing Factory (43)
- Big Bro Array Multiblock (44)
- Mega Stone Breaker (45)
- Big Bro Array Multiblock (46)
- Mega Stone Breaker (47)
- Naquadah Reactor System (48)
- Draconic Crucible Multiblock (49)
- Disassembler Multiblock System (50)
- Draconic Crucible Multiblock (51)
- Block Registry & Casings (52)
- Big Bro Array Multiblock (53)
- Operationalstatus Missing Component
- Ore Processing Factory (55)
- Draconic Crucible Multiblock (56)
- Big Bro Array Multiblock (57)
- Incompact Cyclotron Multiblock (58)
- Astral Computing Array (59)
- Large Neutron Oscillator (60)
- Naquadah Reactor System (61)
- Client Rendering & Models
- Vacuum Drying Furnace
- Block Registry & Casings (64)
- Mega Tree Farm
- Isotopedecayhandler Playertickevent Component
- Starcore Miner Multiblock (67)
- Incompact Cyclotron Multiblock (68)
- Large Neutron Oscillator (69)
- Ore Processing Factory (70)
- Gradlew Script Component
- Naquadah Reactor System (72)

## God Nodes (most connected - your core abstractions)
1. `BigBroArrayMachine` - 53 edges
2. `TSTBlocks` - 53 edges
3. `TSTModern` - 48 edges
4. `TSTRecipeTypes` - 45 edges
5. `DisassemblerRecipeIndex` - 43 edges
6. `GalacticArmillaryMachine` - 37 edges
7. `DraconicCrucibleDragonModel` - 36 edges
8. `GalacticArmillaryGalaxyRender` - 36 edges
9. `DisassemblerRecipes` - 36 edges
10. `AstralComputingArrayMachine` - 27 edges

## Surprising Connections (you probably didn't know these)
- `BigBroArrayPartRender` --references--> `BigBroArrayMachine`  [EXTRACTED]
  src/main/java/com/tstmodern/client/renderer/BigBroArrayPartRender.java → src/main/java/com/tstmodern/machine/BigBroArrayMachine.java
- `BigBroArrayMachine` --references--> `BigBroArrayAddonScanner`  [EXTRACTED]
  src/main/java/com/tstmodern/machine/BigBroArrayMachine.java → src/main/java/com/tstmodern/machine/logic/BigBroArrayAddonScanner.java
- `BigBroArrayMachine` --references--> `BigBroArrayEmbeddedState`  [EXTRACTED]
  src/main/java/com/tstmodern/machine/BigBroArrayMachine.java → src/main/java/com/tstmodern/machine/logic/BigBroArrayEmbeddedState.java
- `BigBroArrayMachine` --references--> `CoreTiers`  [EXTRACTED]
  src/main/java/com/tstmodern/machine/BigBroArrayMachine.java → src/main/java/com/tstmodern/machine/logic/BigBroArrayTierRules.java
- `BigBroArrayAddonScanner` --references--> `AddonPlacement`  [EXTRACTED]
  src/main/java/com/tstmodern/machine/logic/BigBroArrayAddonScanner.java → src/main/java/com/tstmodern/registry/machine/BigBroArrayStructure.java

## Import Cycles
- None detected.

## Communities (75 total, 23 thin omitted)

### Community 0 - "Machine Recipe Systems (0)"
Cohesion: 0.06
Nodes (84): assembler_recipes, assembly_line_recipes, autoclave_recipes, blast_recipes, Block, bolt, cablegtsingle, centrifuge_recipes (+76 more)

### Community 1 - "Draconic Crucible Multiblock (1)"
Cohesion: 0.06
Nodes (29): com.google.gson.JsonArray, cubedeformation, cubelistbuilder, inputstreamreader, jsonobject, jsonparser, layerdefinition, meshdefinition (+21 more)

### Community 2 - "Machine Recipe Systems (2)"
Cohesion: 0.12
Nodes (45): abilities, blockbehaviour, blockentry, blockinfo, blockitem, blocks, com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition, com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate (+37 more)

### Community 3 - "Machine Recipe Systems (3)"
Cohesion: 0.06
Nodes (26): bifunction, com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability, com.gregtechceu.gtceu.api.recipe.chance.boost.ChanceBoostFunction, com.gregtechceu.gtceu.api.recipe.chance.logic.ChanceLogic, com.gregtechceu.gtceu.api.recipe.content.Content, com.mojang.blaze3d.vertex.VertexConsumer, it.unimi.dsi.fastutil.objects.Object2IntMap, java.util.function.IntUnaryOperator (+18 more)

### Community 4 - "Machine Recipe Systems (4)"
Cohesion: 0.04
Nodes (60): aluminium, annealedcopper, bauxite, blacksteel, calcium, calciumchloride, casing_stainless_clean, casing_titanium_gearbox (+52 more)

### Community 5 - "Astral Computing Array (5)"
Cohesion: 0.06
Nodes (21): com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider, com.gregtechceu.gtceu.api.machine.feature.IMachineLife, com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine, com.lowdragmc.lowdraglib.gui.widget.ProgressWidget, java.util.function.BooleanSupplier, java.util.function.DoubleSupplier, mth, net.minecraft.client.gui.GuiGraphics (+13 more)

### Community 6 - "Machine Recipe Systems (6)"
Cohesion: 0.06
Nodes (44): at, biometags, cachebuilder, cacheloader, com.google.common.cache.LoadingCache, com.google.gson.JsonElement, com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory, com.gregtechceu.gtceu.integration.jei.recipe.GTRecipeJEICategory (+36 more)

### Community 7 - "Machine Recipe Systems (7)"
Cohesion: 0.12
Nodes (15): com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder, DisassemblerRecipes, EutSpec, FluidInputSpec, ItemInputSpec, ItemRef, RecipeSpec, RecipeType (+7 more)

### Community 8 - "Machine Recipe Systems (8)"
Cohesion: 0.05
Nodes (39): americium, argon, blaze, carbon, casing_invar_heatproof, casing_polytetrafluoroethylene_pipe, casing_titanium_stable, cracking_recipes (+31 more)

### Community 9 - "Machine Recipe Systems (9)"
Cohesion: 0.07
Nodes (32): arraylist, BufferSource, builtinregistries, collection, collections, com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder, completablefuture, executor (+24 more)

### Community 10 - "Incompact Cyclotron Multiblock (10)"
Cohesion: 0.14
Nodes (13): com.gregtechceu.gtceu.api.machine.MetaMachine, com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine, com.gregtechceu.gtceu.api.recipe.content.ContentModifier, com.gregtechceu.gtceu.api.recipe.GTRecipe, com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction, overclockinglogic, parallellogic, recipehelper (+5 more)

### Community 11 - "Naquadah Reactor System (11)"
Cohesion: 0.09
Nodes (16): com.gregtechceu.gtceu.api.machine.trait.RecipeLogic, com.gregtechceu.gtceu.api.misc.EnergyContainerList, com.gregtechceu.gtceu.api.recipe.ActionResult, com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder, direction, eurecipecapability, ienergycontainer, multiblockdisplaytext (+8 more)

### Community 12 - "Starcore Miner Multiblock (12)"
Cohesion: 0.11
Nodes (10): ICustomRecipeLogic, MegaNaquadahReactorLogic, StarcoreMinerLogic, WeightedEntry, Fuel, Override, MegaNaquadahReactorRecipeLogic, Modifier (+2 more)

### Community 13 - "Disassembler Multiblock System (13)"
Cohesion: 0.15
Nodes (8): net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier, net.minecraft.world.item.Item, DisassemblerRecipeIndex, Override, SuppressWarnings, RepresentativePlan, RuntimeRecipePlan, Snapshot

### Community 14 - "Disassembler Multiblock System (14)"
Cohesion: 0.11
Nodes (5): arrays, collectors, DisassemblerStructure, DraconicCrucibleStructure, GalacticArmillaryStructure

### Community 16 - "Disassembler Multiblock System (16)"
Cohesion: 0.13
Nodes (11): com.gregtechceu.gtceu.api.recipe.GTRecipeType, net.minecraft.resources.ResourceLocation, BigBroArrayMachineCatalog, Entry, Family, BigBroArrayMode, GENERATOR, PROCESSOR (+3 more)

### Community 17 - "Incompact Cyclotron Multiblock (17)"
Cohesion: 0.19
Nodes (17): com.gregtechceu.gtceu.client.model.machine.IControllerModelRenderer, com.gregtechceu.gtceu.client.renderer.machine.DynamicRender, com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType, com.mojang.serialization.Codec, hashmap, map, modelutils, net.minecraft.client.resources.model.BakedModel (+9 more)

### Community 18 - "Block Registry & Casings (18)"
Cohesion: 0.09
Nodes (19): ClientTickEvent, concurrenthashmap, dynamicrendermanager, iterator, materialblock, materialblockitem, materialpipeblock, minecraftforge (+11 more)

### Community 19 - "Incompact Cyclotron Multiblock (19)"
Cohesion: 0.17
Nodes (8): com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController, com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart, net.minecraft.core.Direction, net.minecraft.world.level.block.state.BlockState, GalacticArmillaryDefinition, IncompactCyclotronDefinition, LargeNeutronOscillatorDefinition, OreProcessingFactoryDefinition

### Community 20 - "Galactic Armillary Multiblock (20)"
Cohesion: 0.15
Nodes (10): com.mojang.blaze3d.vertex.PoseStack, net.minecraft.client.renderer.texture.TextureAtlasSprite, net.minecraft.world.phys.AABB, org.joml.Vector3f, Asteroid, Cloud, GalacticArmillaryGalaxyRender, Override (+2 more)

### Community 21 - "Draconic Crucible Multiblock (21)"
Cohesion: 0.10
Nodes (22): axis, blockpos, bufferbuilder, dist, inventorymenu, itemblockrendertypes, lighttexture, logutils (+14 more)

### Community 22 - "Incompact Cyclotron Multiblock (22)"
Cohesion: 0.11
Nodes (9): com.gregtechceu.gtceu.api.addon.GTAddon, com.gregtechceu.gtceu.api.addon.IGTAddon, BigBroArrayRecipes, IncompactCyclotronRecipes, MassFabricatorRecipes, NetherInterfaceRecipes, TSTWorldgen, Override (+1 more)

### Community 23 - "Disassembler Multiblock System (23)"
Cohesion: 0.12
Nodes (12): com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider, com.gregtechceu.gtceu.api.gui.fancy.TooltipsPanel, com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine, com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine, com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine, com.lowdragmc.lowdraglib.gui.modular.ModularUI, ConfiguratorPanel, CasingTier (+4 more)

### Community 24 - "Item Registry & Components"
Cohesion: 0.13
Nodes (16): item, mezz.jei.api.IModPlugin, mezz.jei.api.JeiPlugin, mezz.jei.api.recipe.advanced.IRecipeManagerPlugin, mezz.jei.api.recipe.category.IRecipeCategory, mezz.jei.api.recipe.IFocus, mezz.jei.api.recipe.RecipeType, mezz.jei.api.registration.IAdvancedRegistration (+8 more)

### Community 25 - "Big Bro Array Multiblock (25)"
Cohesion: 0.10
Nodes (18): com.gregtechceu.gtceu.api.machine.MachineDefinition, com.lowdragmc.lowdraglib.gui.texture.ResourceTexture, gtceuapi, gtmachineutils, gtrecipeserializer, gtrecipetypes, position, progresstexture (+10 more)

### Community 26 - "Disassembler Multiblock System (26)"
Cohesion: 0.17
Nodes (19): chatformatting, com.gregtechceu.gtceu.api.machine.TickableSubscription, com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler, com.lowdragmc.lowdraglib.gui.widget.Widget, componentpanelwidget, descsynced, draggablescrollablewidgetgroup, gtresearchmachines (+11 more)

### Community 27 - "Big Bro Array Multiblock (27)"
Cohesion: 0.15
Nodes (16): blocklookup, blockstate, com.gregtechceu.gtceu.api.capability.recipe.IO, compoundtag, fluidaction, fluidhandlerlist, imultipart, itembuspartmachine (+8 more)

### Community 28 - "Starcore Miner Multiblock (28)"
Cohesion: 0.15
Nodes (5): com.gregtechceu.gtceu.api.machine.multiblock.PartAbility, java.lang.reflect.Field, Override, StarcoreMinerMachine, PartAbilities

### Community 29 - "Big Bro Array Multiblock (29)"
Cohesion: 0.19
Nodes (10): net.minecraft.nbt.CompoundTag, net.minecraft.world.item.ItemStack, net.minecraftforge.items.IItemHandlerModifiable, BigBroArrayEmbeddedState, BigBroArrayMachineTransfer, ExtractionStep, InsertionStep, LoadResult (+2 more)

### Community 31 - "Big Bro Array Multiblock (31)"
Cohesion: 0.16
Nodes (8): base64, bytearrayinputstream, gzipinputstream, ioexception, AstralComputingArrayStructure, MegaNaquadahReactorStructure, StarcoreMinerStructure, standardcharsets

### Community 32 - "Material Registry & Elements"
Cohesion: 0.15
Nodes (14): com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent, com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet, com.gregtechceu.gtceu.api.data.chemical.material.Material, dust, dustproperty, fluidbuilder, fluidproperty, fluidstate (+6 more)

### Community 33 - "Ore Processing Factory (33)"
Cohesion: 0.12
Nodes (9): energyhatchpartmachine, fluid, fluidrecipecapability, fluids, fluidstack, laserhatchpartmachine, net.minecraftforge.fluids.capability.IFluidHandler, MegaStoneBreakerLogic (+1 more)

### Community 34 - "Naquadah Reactor System (34)"
Cohesion: 0.13
Nodes (4): com.gregtechceu.gtceu.api.machine.IMachineBlockEntity, com.gregtechceu.gtceu.api.machine.multiblock.CoilWorkableElectricMultiblockMachine, GiantVacuumDryingFurnaceMachine, GiantVacuumDryingFurnaceLogic

### Community 35 - "Naquadah Reactor System (35)"
Cohesion: 0.24
Nodes (3): StationRecipeBuilder, NaquadahFuelRefineryRecipes, TSTCircuitTags

### Community 36 - "Big Bro Array Multiblock (36)"
Cohesion: 0.20
Nodes (4): AddonPlacement, BigBroArrayStructure, PlacementSeed, RelativeCell

### Community 37 - "Disassembler Multiblock System (37)"
Cohesion: 0.19
Nodes (5): DescriptorProvider, DisassemblerRecipeSource, FunctionalInterface, DisassemblerRecipeSources, Snapshot

### Community 38 - "Big Bro Array Multiblock (38)"
Cohesion: 0.26
Nodes (4): net.minecraft.core.BlockPos, BigBroArrayAddonMatcher, BlockLookup, BlockRules

### Community 39 - "Galactic Armillary Multiblock (39)"
Cohesion: 0.26
Nodes (3): net.minecraft.data.recipes.FinishedRecipe, GalacticArmillaryRecipes, HyperThermalConvectorRecipes

### Community 40 - "Big Bro Array Multiblock (40)"
Cohesion: 0.24
Nodes (5): objects, AddonMatch, BigBroArrayAddonScanner, BigBroArrayAddonState, CoreTiers

### Community 41 - "Disassembler Multiblock System (41)"
Cohesion: 0.24
Nodes (8): DecodedFluidContent, DecodedItemContent, DecodedRecipe, DisassemblerRecipeAdapter, ItemAmount, SuppressWarnings, DescriptorEnumerator, FunctionalInterface

### Community 42 - "Disassembler Multiblock System (42)"
Cohesion: 0.17
Nodes (7): net.minecraft.world.level.material.Fluid, DisassemblerRecipeDescriptor, ReturnedFluid, ReturnedItem, DisassemblerSpecialRecipes, SuppressWarnings, TagKey

### Community 43 - "Ore Processing Factory (43)"
Cohesion: 0.20
Nodes (5): Override, OreProcessingFactoryPartRender, FluidAction, Override, OreProcessingFactoryMachine

### Community 45 - "Mega Stone Breaker (45)"
Cohesion: 0.23
Nodes (3): FluidAction, Override, MegaStoneBreakerMachine

### Community 46 - "Big Bro Array Multiblock (46)"
Cohesion: 0.20
Nodes (7): coilblock, forgeregistries, net.minecraft.world.item.CreativeModeTab, net.minecraftforge.eventbus.api.IEventBus, net.minecraftforge.registries.DeferredRegister, resourcelocation, TSTCreativeModeTabs

### Community 48 - "Naquadah Reactor System (48)"
Cohesion: 0.20
Nodes (3): com.gregtechceu.gtceu.api.pattern.util.PatternMatchContext, NaquadahFuelRefineryLogic, NaquadahFuelRefineryDefinition

### Community 49 - "Draconic Crucible Multiblock (49)"
Cohesion: 0.29
Nodes (3): net.minecraft.tags.TagKey, DraconicCrucibleRecipes, GTRecipeBuilder

### Community 52 - "Block Registry & Casings (52)"
Cohesion: 0.40
Nodes (3): net.minecraft.client.renderer.block.model.BakedQuad, net.minecraft.client.renderer.MultiBufferSource, InfinityHaloRenderer

### Community 54 - "Operationalstatus Missing Component"
Cohesion: 0.20
Nodes (7): OperationalStatus, CAN_RUN, FRAME_TOO_LOW, MISSING_INPUT_ENERGY, MISSING_OUTPUT_ENERGY, NO_MACHINE, STALE_ID

### Community 56 - "Draconic Crucible Multiblock (56)"
Cohesion: 0.29
Nodes (5): Builder, net.minecraft.world.level.block.state.properties.BooleanProperty, DraconicCrucibleCoreBlock, Override, statedefinition

### Community 58 - "Incompact Cyclotron Multiblock (58)"
Cohesion: 0.46
Nodes (3): IncompactCyclotronPartRender, Override, IncompactCyclotronMachine

### Community 64 - "Block Registry & Casings (64)"
Cohesion: 0.29
Nodes (6): Failure, MISSING_BLOCK, MIXED_TIER, NONE, UNLOADED, WRONG_BLOCK

### Community 71 - "Gradlew Script Component"
Cohesion: 0.83
Nodes (3): gradlew script, die(), warn()

## Knowledge Gaps
- **24 isolated node(s):** `CUSTOM`, `MOD_ORIGINAL`, `ASSEMBLER`, `ASSEMBLY_LINE`, `NONE` (+19 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 378 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **23 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `BigBroArrayMachine` connect `Big Bro Array Multiblock (30)` to `Machine Recipe Systems (2)`, `Big Bro Array Multiblock (38)`, `Big Bro Array Multiblock (40)`, `Incompact Cyclotron Multiblock (10)`, `Naquadah Reactor System (11)`, `Big Bro Array Multiblock (44)`, `Disassembler Multiblock System (16)`, `Incompact Cyclotron Multiblock (17)`, `Disassembler Multiblock System (23)`, `Disassembler Multiblock System (26)`, `Big Bro Array Multiblock (27)`, `Big Bro Array Multiblock (29)`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **Why does `TSTBlocks` connect `Machine Recipe Systems (2)` to `Machine Recipe Systems (0)`, `Machine Recipe Systems (4)`, `Machine Recipe Systems (8)`, `Disassembler Multiblock System (13)`, `Big Bro Array Multiblock (46)`, `Block Registry & Casings (18)`, `Draconic Crucible Multiblock (51)`, `Draconic Crucible Multiblock (21)`, `Disassembler Multiblock System (23)`, `Disassembler Multiblock System (26)`?**
  _High betweenness centrality (0.042) - this node is a cross-community bridge._
- **Why does `DisassemblerRecipes` connect `Machine Recipe Systems (7)` to `Machine Recipe Systems (0)`, `Incompact Cyclotron Multiblock (22)`?**
  _High betweenness centrality (0.038) - this node is a cross-community bridge._
- **What connects `CUSTOM`, `MOD_ORIGINAL`, `ASSEMBLER` to the rest of the system?**
  _24 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Machine Recipe Systems (0)` be split into smaller, more focused modules?**
  _Cohesion score 0.055523199378761406 - nodes in this community are weakly interconnected._
- **Should `Draconic Crucible Multiblock (1)` be split into smaller, more focused modules?**
  _Cohesion score 0.05651176133103844 - nodes in this community are weakly interconnected._
- **Should `Machine Recipe Systems (2)` be split into smaller, more focused modules?**
  _Cohesion score 0.1156773211567732 - nodes in this community are weakly interconnected._