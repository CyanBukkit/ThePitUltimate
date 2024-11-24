package cn.charlotte.pit

import cn.charlotte.pit.addon.AddonHandler
import cn.charlotte.pit.command.PitAdminCommands
import cn.charlotte.pit.command.PitAdminDupeFixCommands
import cn.charlotte.pit.command.PitAdminSimpleCommand
import cn.charlotte.pit.command.PitCommands
import cn.charlotte.pit.command.handler.HandHasItem
import cn.charlotte.pit.command.handler.HandHasItemValidator
import cn.charlotte.pit.command.handler.metaKey
import cn.charlotte.pit.config.NewConfiguration
import cn.charlotte.pit.data.CDKData
import cn.charlotte.pit.enchantment.type.aqua.ClubRodEnchant
import cn.charlotte.pit.enchantment.type.aqua.GrandmasterEnchant
import cn.charlotte.pit.enchantment.type.aqua.LuckOfPondEnchant
import cn.charlotte.pit.enchantment.type.aqua.RogueEnchant
import cn.charlotte.pit.enchantment.type.dark_normal.*
import cn.charlotte.pit.enchantment.type.dark_rare.CatastrophicEnchant
import cn.charlotte.pit.enchantment.type.dark_rare.ComboDazzlingGoldEnchant
import cn.charlotte.pit.enchantment.type.dark_rare.ComboDustEnchant
import cn.charlotte.pit.enchantment.type.dark_rare.ComboRottenHeartEnchant
import cn.charlotte.pit.enchantment.type.dark_rare.ComboUnpredictablyEnchant
import cn.charlotte.pit.enchantment.type.dark_rare.ComboVenomEnchant
import cn.charlotte.pit.enchantment.type.dark_rare.GoldenHandcuffsEnchant
import cn.charlotte.pit.enchantment.type.genesis.*
import cn.charlotte.pit.enchantment.type.limit.Limit24520Ench
import cn.charlotte.pit.enchantment.type.limit.xzq.LimitXZQ1Ench
import cn.charlotte.pit.enchantment.type.normal.*
import cn.charlotte.pit.enchantment.type.op.*
import cn.charlotte.pit.enchantment.type.rage.*
import cn.charlotte.pit.enchantment.type.ragerare.Regularity
import cn.charlotte.pit.enchantment.type.ragerare.ThinkOfThePeopleEnchant
import cn.charlotte.pit.enchantment.type.rare.*
import cn.charlotte.pit.enchantment.type.sewer_normal.AegisEnchant
import cn.charlotte.pit.enchantment.type.sewer_rare.TrashPandaEnchant
import cn.charlotte.pit.enchantment.type.special.SoulRipperEnchant
import cn.charlotte.pit.events.genesis.listener.GenesisCombatListener
import cn.charlotte.pit.events.impl.*
import cn.charlotte.pit.events.impl.major.*
import cn.charlotte.pit.hologram.HologramListener
import cn.charlotte.pit.hook.PitPapiHook
import cn.charlotte.pit.item.AbstractPitItem
import cn.charlotte.pit.item.type.*
import cn.charlotte.pit.listener.*
import cn.charlotte.pit.map.kingsquests.KingsQuests
import cn.charlotte.pit.menu.shop.button.type.BowBundleShopButton
import cn.charlotte.pit.menu.shop.button.type.CombatSpadeShopButton
import cn.charlotte.pit.menu.shop.button.type.PantsBundleShopButton
import cn.charlotte.pit.menu.shop.button.type.SwordBundleShopButton
import cn.charlotte.pit.menu.trade.TradeListener
import cn.charlotte.pit.nametag.NameTagImpl
import cn.charlotte.pit.npc.type.*
import cn.charlotte.pit.perk.type.boost.*
import cn.charlotte.pit.perk.type.prestige.*
import cn.charlotte.pit.perk.type.shop.*
import cn.charlotte.pit.perk.type.streak.beastmode.BeastModeMegaStreak
import cn.charlotte.pit.perk.type.streak.beastmode.RAndRKillStreak
import cn.charlotte.pit.perk.type.streak.beastmode.TacticalRetreatKillStreak
import cn.charlotte.pit.perk.type.streak.beastmode.ToughSkinKillStreak
import cn.charlotte.pit.perk.type.streak.grandfinale.ApostleForTheGesusKillStreak
import cn.charlotte.pit.perk.type.streak.grandfinale.AssuredStrikeKillStreak
import cn.charlotte.pit.perk.type.streak.grandfinale.GrandFinaleMegaStreak
import cn.charlotte.pit.perk.type.streak.grandfinale.LeechKillStreak
import cn.charlotte.pit.perk.type.streak.hermit.*
import cn.charlotte.pit.perk.type.streak.highlander.GoldNanoFactoryKillStreak
import cn.charlotte.pit.perk.type.streak.highlander.HighlanderMegaStreak
import cn.charlotte.pit.perk.type.streak.highlander.KhanateKillStreak
import cn.charlotte.pit.perk.type.streak.highlander.WitherCraftKillStreak
import cn.charlotte.pit.perk.type.streak.nonpurchased.*
import cn.charlotte.pit.perk.type.streak.tothemoon.SuperStreaker
import cn.charlotte.pit.perk.type.streak.tothemoon.ToTheMoonMegaStreak
import cn.charlotte.pit.perk.type.streak.uber.UberStreak
import cn.charlotte.pit.quest.type.*
import cn.charlotte.pit.runnable.*
import cn.charlotte.pit.scoreboard.Scoreboard
import cn.charlotte.pit.sound.impl.*
import cn.charlotte.pit.util.getInstance
import cn.charlotte.pit.util.hologram.packet.PacketHologramRunnable
import cn.charlotte.pit.util.menu.ButtonListener
import cn.charlotte.pit.util.nametag.NametagHandler
import cn.charlotte.pit.util.scoreboard.Assemble
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages
import dev.rollczi.litecommands.meta.Meta
import dev.rollczi.litecommands.validator.ValidatorScope
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginDescriptionFile
import xyz.refinedev.spigot.CarbonSpigot

object PitHook {
    @JvmStatic
    val gitVersion = "53c934dac"
    @JvmStatic
    val itemVersion = "ShanguanLingV1uuid"
    fun init() {
        try {
            NewConfiguration.loadFile()
            NewConfiguration.load()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        loadEnchants()
        loadPerks()
        loadItems()
        loadNameTag()
        loadScoreBoard()
        loadQuests()
        loadEvents()
        registerListeners()
        loadRunnable()
        registerSounds()

        loadCommands()

        loadNpcs()

        Bukkit.getPluginManager().getPlugin("PlaceholderAPI")?.let {
            PitPapiHook.register()
        }

        val description = ThePit.getInstance().description

        val field = PluginDescriptionFile::class.java.getDeclaredField("version")
        field.isAccessible = true
        field.set(description, gitVersion)

        AddonHandler.start()

        ActionBarDisplayRunnable.start()

        KingsQuests.enable()

        CDKData.loadAllCDKFromData()

        Bukkit.getPluginManager().registerEvents(SewersRunnable, ThePit.getInstance())
        SewersRunnable.runTaskTimer(ThePit.getInstance(), 20L, 20L)
        //CleanupDupeEnch0525Runnable.runTaskTimer(ThePit.getInstance(), 20L, 20L)
        SpecialPlayerRunnable.runTaskTimer(ThePit.getInstance(), 1L, 1L)
        PrivatePlayerRunnable.runTaskTimer(ThePit.getInstance(),1L,1L)
    }

    private fun loadCommands() {
        LiteBukkitFactory.builder()
            .commands(
                PitAdminSimpleCommand(),
                PitAdminCommands(),
                PitCommands(),
                PitAdminDupeFixCommands()
            )
            .settings {
                it.nativePermissions(true)
            }
            .message(LiteBukkitMessages.INVALID_USAGE) { inv, ctx ->
                return@message "§c用法: ".plus(buildString {
                    if (ctx.schematic.isOnlyFirst) {
                        append(ctx.schematic.first())
                    } else {
                        appendLine()
                        ctx.schematic.all().forEach {
                            appendLine(" §c$it")
                        }
                    }
                })
            }
            .message(LiteBukkitMessages.INVALID_NUMBER) { input ->
                "§c错误的数字: $input"
            }
            .message(LiteBukkitMessages.MISSING_PERMISSIONS, "Unknown command. Type \"/help\" for help.")
            .message(LiteBukkitMessages.PLAYER_NOT_FOUND) { input ->
                "§c未找到名为 $input 的玩家"
            }
            .message(LiteBukkitMessages.PLAYER_ONLY, "§cOnly Player Use")
            .validator(ValidatorScope.of(HandHasItemValidator::class.java), HandHasItemValidator())
            .annotations {
                it.processor { invoker ->
                    invoker.on(HandHasItem::class.java) { handHasItem, metaHolder/*, executorProvider*/ ->
                        metaHolder.meta().also { meta ->
                            meta.put(metaKey, handHasItem)
                            meta.listEditor(Meta.VALIDATORS).add(HandHasItemValidator::class.java).apply()
                        }
                    }
                }
            }.build()
    }

    private fun loadRunnable() {

        EventRunnable().runTaskTimerAsynchronously(ThePit.getInstance(), 60L, 120L)

        AnnouncementRunnable.runTaskTimerAsynchronously(ThePit.getInstance(), 0, 40 * 60)
        GoldDropRunnable().runTaskTimer(ThePit.getInstance(), 20, 20)

        ProtectRunnable().runTaskTimer(ThePit.getInstance(), 20, 20)

        FreeExpRunnable().runTaskTimer(ThePit.getInstance(), 20 * 60 * 15, 20 * 60 * 15)
        NightVisionRunnable().runTaskTimer(ThePit.getInstance(), 20, 20)

        PacketHologramRunnable().runTaskTimerAsynchronously(ThePit.getInstance(), 20, 20)

        BountyRunnable().runTaskTimerAsynchronously(ThePit.getInstance(), 5, 5)
    }

    private fun loadItems() {
        for (clazz in listOf(
            AngelChestplate::class.java,
            ArmageddonBoots::class.java,
            BountySolventPotion::class.java,
            ChunkOfVileItem::class.java,
            FunkyFeather::class.java,
            GoldenHelmet::class.java,
            JewelSword::class.java,
            JumpBoostPotion::class.java,
            MythicRepairKit::class.java,
            PitCactus::class.java,
            SpireArmor::class.java,
            SuperPackage::class.java,
            TotallyLegitGem::class.java,
            GlobalAttentionGem::class.java,
            UberDrop::class.java,
            MythicEnchantingTable::class.java
        )) {
            try {
                val pitItem = clazz.getInstance()
                if (pitItem is AbstractPitItem) {
                    if (pitItem is Listener) {
                        Bukkit.getPluginManager().registerEvents(pitItem, ThePit.getInstance())
                    }

                    ThePit.getInstance().itemFactor.itemMap[pitItem.internalName] = clazz as Class<AbstractPitItem>
                }
            } catch (e: Exception) {

            }
        }

        val clazz = LuckyChestplate::class.java
        val pitItem = clazz.getInstance()
        if (pitItem is AbstractPitItem) {
            if (pitItem is Listener) {
                Bukkit.getPluginManager().registerEvents(pitItem, ThePit.getInstance())
            }

            ThePit.getInstance().itemFactor.itemMap[pitItem.internalName] = clazz as Class<AbstractPitItem>
        }
    }

    private fun loadEnchants() {
        val enchantmentFactor = ThePit.getInstance().enchantmentFactor
        val classes = mutableListOf(
            //new dark_rare
            CatastrophicEnchant::class.java,
            ComboUnpredictablyEnchant::class.java,
            ComboDazzlingGoldEnchant::class.java,
            ComboRottenHeartEnchant::class.java,

            //rare
            NightFallEnchant::class.java,
            MysticRealmEnchant::class.java,
            TheSwiftWindEnchant::class.java,
            MinersEnchant::class.java,
            SoulEarterEnchant::class.java,
            EscapeEnchant::class.java,
            UndeadArrowEnchant::class.java,
            ComboComaEnchant::class.java,
            AssassinsEnchant::class.java,
            CriticalHitEnchant::class.java,
            ComboStealEnchant::class.java,
            BloodFeatherEnchant::class.java,


            //normal
            PhantomShieldEnchant::class.java,
            KingKillersEnchant::class.java,
            SanctionEnchant::class.java,

            //end
            ClubRodEnchant::class.java,
            GrandmasterEnchant::class.java,
            LuckOfPondEnchant::class.java,
            RogueEnchant::class.java,
            Regularity::class.java,
            ThinkOfThePeopleEnchant::class.java,
            NewDealEnchant::class.java,
            ReallyToxicEnchant::class.java,
            SingularityEnchant::class.java,
            GrimReaperEnchant::class.java,
            HedgeFundEnchant::class.java,
            MindAssaultEnchant::class.java,
            MiseryEnchant::class.java,
            SanguisugeEnchant::class.java,
            SomberEnchant::class.java,
            SpiteEnchant::class.java,
            ComboVenomEnchant::class.java,
            GoldenHandcuffsEnchant::class.java,
            EvilWithinEnchant::class.java,
            GuardianEnchant::class.java,
            JerryEnchant::class.java,
            JerryEnchant2::class.java,
            JerryEnchant3::class.java,
            JerryEnchant4::class.java,
            JerryEnchant5::class.java,
            AntiAbsorptionEnchant::class.java,
            AntiBowSpammerEnchantP::class.java,
            AntiBowSpammerEnchantW::class.java,
            AntiMythicismEnchant::class.java,
            ArrowArmoryEnchant::class.java,
            PrisonBossEnchant::class.java,
            BillyEnchant::class.java,
            BooBooEnchant::class.java,
            BountyHunterEnchant::class.java,
            BowComboEnchant::class.java,
            BruiserEnchant::class.java,
            BerserkerEnchant::class.java,
            BulletTimeEnchant::class.java,
            ComboDamageEnchant::class.java,
            ComboHealEnchant::class.java,
            ComboSwiftEnchant::class.java,
            CounterJanitorEnchant::class.java,
            CounterOffensiveEnchant::class.java,
            CreativeEnchant::class.java,
            CriticallyFunkyEnchant::class.java,
            CriticallyRichEnchant::class.java,
            CrushEnchant::class.java,
            DavidAndGoliathEnchant::class.java,
            DiamondAllergyEnchant::class.java,
            DiamondBreakerEnchant::class.java,
            ElectrolytesEnchant::class.java,
            EndlessQuiverEnchant::class.java,
            FractionalReserveEnchant::class.java,
            GoldExplorerEnchant::class.java,
            GutsEnchant::class.java,
            HermesEnchant::class.java,
            HuntTheHunterEnchant::class.java,
            LifeStealEnchant::class.java,
            LureEnchant::class.java,
            MirrorEnchant::class.java,
            MixedCombatEnchant::class.java,
            NotGladiatorEnchant::class.java,
            OverHealEnchant::class.java,
            PantsRadarEnchant::class.java,
            ParasiteEnchant::class.java,
            PebbleEnchant::class.java,
            PeroxideEnchant::class.java,
            PitMBAEnchant::class.java,
            PitPocketEnchant::class.java,
            PowerEnchant::class.java,
            ProtectionEnchant::class.java,
            PurpleGoldEnchant::class.java,
            ReaperEnchant::class.java,
            ResentmentEnchant::class.java,
            RespawnAbsorptionEnchant::class.java,
            RespawnResistanceEnchant::class.java,
            RustBowEnchant::class.java,
            SelfCheckoutEnchant::class.java,
            SharkEnchant::class.java,
            SharpnessEnchant::class.java,
            SierraEnchant::class.java,
            SniperEnchant::class.java,
            SpeedyKillEnchant::class.java,
            SprintDrainEnchant::class.java,
            CriticalMomentEnchant::class.java,
            StrikeGoldEnchant::class.java,
            ThornsEnchant::class.java,
            ThumpEnchant::class.java,
            TNTEnchant::class.java,
            TrueDamageArrowEnchant::class.java,
            UnBreakEnchant::class.java,
            WaspEnchant::class.java,
            WisdomEnchant::class.java,
            BlazingAngelEnchant::class.java,
            BounceBowEnchant::class.java,
            DJBundlePVZ::class.java,
            EchoOfSnowlandPEnchant::class.java,
            EchoOfSnowlandWEnchant::class.java,
            EmergencyColonyEnchant::class.java,
            KFCBoomerEnchant::class.java,
            LaserEnchant::class.java,
            MultiExchangeLocationEnchant::class.java,
            OPDamageEnchant::class.java,
            PowerAngelEnchant::class.java,
            StarJudgementEnchant::class.java,
            SuperLaserEnchant::class.java,
            SuperSlimeEnchant::class.java,
            TestEnchant::class.java,
            AbsorptionEnchant::class.java,
            ArchangelEnchant::class.java,
            AssassinEnchant::class.java,
            ComboDustEnchant::class.java,
            BillionaireEnchant::class.java,
            ComboStrikeEnchant::class.java,
            ComboStunEnchant::class.java,
           MicroDegravityEnchant::class.java,
            DivineMiracleEnchant::class.java,
            EnderBowEnchant::class.java,
            ExecutionerEnchant::class.java,
            FightOrDieEnchant::class.java,
            GambleEnchant::class.java,
            GomrawsHeartEnchant::class.java,
            HealerEnchant::class.java,
            HealShieldEnchant::class.java,
            HemorrhageEnchant::class.java,
            InstaBoomEnchant::class.java,
            LuckyShotEnchant::class.java,
            MegaLongBowEnchant::class.java,
            PaparazziEnchant::class.java,
            PullBowEnchant::class.java,
            SlimeEnchant::class.java,
            SnowballsEnchant::class.java,
            SolitudeEnchant::class.java,
            SpeedyHitEnchant::class.java,
            ThePunchEnchant::class.java,
            HyperOxygenEnchant::class.java,
            VolleyEnchant::class.java,
            AegisEnchant::class.java,
            SoulRipperEnchant::class.java,
            DemonHenEnchant::class.java,
            AceOfSpades::class.java,
            CricketEnchant::class.java,
            Brakes::class.java,
            SeriousSituationEnchant::class.java,
            BreachingChargeEnchant::class.java
        )

        /*        classes += HappyNewYearEnchant::class.java
                classes += WitheredAndPiercingThroughTheHeart::class.java
                classes += LastShadowLeapForward::class.java

                classes += RealManEnchant::class.java*/
        classes += TrotEnchant::class.java
        classes += TrytoGiveEnchant::class.java

        classes += BreakArmorEnchant::class.java
        classes += CoinGloriousEnchant::class.java
        classes += ComboBacktrackEnchant::class.java
        classes += DoubleJumpEnchant::class.java
        classes += SacredArrowEnchant::class.java
        classes += TrashPandaEnchant::class.java
        classes += JerryEnchant5::class.java

        classes += Limit24520Ench::class.java
        classes += LimitXZQ1Ench::class.java

        enchantmentFactor.init(LinkedHashSet(classes))
    }

    private fun loadScoreBoard() {
        val assemble = Assemble(ThePit.getInstance(), Scoreboard())
        assemble.ticks = 4
    }

    private fun loadNameTag() {
        val nametagHandler = NametagHandler(ThePit.getInstance(), NameTagImpl())
        nametagHandler.ticks = 20
    }

    private fun loadPerks() {
        val perkFactory = ThePit.getInstance().perkFactory
        val classes = listOf<Class<*>>(
            BowBoostPerk::class.java,
            BuildBattlerBoostPerk::class.java,
            CoinBoostPerk::class.java,
            CoinContractBoostPerk::class.java,
            CoinPrestigeBoostPerk::class.java,
            DmgReduceBoostPerk::class.java,
            ElGatoBoostPerk::class.java,
            MeleeBoostPerk::class.java,
            XPBoostPerk::class.java,
            XPContractBoostPerk::class.java,
            XPPrestigeBoostPerk::class.java,
            ArrowArmoryPerk::class.java,
            AssistantToTheStreakerPerk::class.java,
            AutoBuyPerk::class.java,
            BarbarianPerk::class.java,
            BeastModeBundlePerk::class.java,
            BountySolventShopPerk::class.java,
            CelebrityPerk::class.java,
            CombatSpadePerk::class.java,
            ContractorPerk::class.java,
            CoolPerk::class.java,
            CoopCatPerk::class.java,
            DiamondLeggingsShopPerk::class.java,
            DirtyPerk::class.java,
            DivineInterventionPerk::class.java,
            ExtraEnderchestPerk::class.java,
            ExtraHeartPerk::class.java,
            ExtraKillStreakSlotPerk::class.java,
            ExtraPerkSlotPerk::class.java,
            FastPassPerk::class.java,
            FirstAidEggPerk::class.java,
            FirstStrikePerk::class.java,
            FishClubPerk::class.java,
            GoldPickaxePerk::class.java,
            GrandFinaleBundlePerk::class.java,
            HeresyPerk::class.java,
            HermitBundlePerk::class.java,
            HighlanderBundlePerk::class.java,
            ImpatientPerk::class.java,
            IronPackShopPerk::class.java,
            JumpBoostShopPerk::class.java,
//            KungFuKnowledgePerk::class.java,
            MarathonPerk::class.java,
            MonsterPerk::class.java,
            MythicismPerk::class.java,
            ObsidianStackShopPerk::class.java,
            OlympusPerk::class.java,
            PantsBundleShopPerk::class.java,
            SwrodBundleShopPerk::class.java,
            BowBundleShopPerk::class.java,
            PromotionPerk::class.java,
            PureRage::class.java,
            RamboPerk::class.java,
            RawNumbersPerk::class.java,
            ReconPerk::class.java,
            ScamArtistPerk::class.java,
            SelfConfidencePerk::class.java,
            TacticalInsertionsPerk::class.java,
            TastySoupPerk::class.java,
            TenacityPerk::class.java,
            TheWayPerk::class.java,
            ThickPerk::class.java,
            ToTheMoonBundle::class.java,
            YummyPerk::class.java,
            ArrowRecoveryPerk::class.java,
            BountyHunterPerk::class.java,
            FishingRodPerk::class.java,
            GladiatorPerk::class.java,
            GoldenHeadPerk::class.java,
            GoldMinerPerk::class.java,
            LuckyDiamondPerk::class.java,
            MinerPerk::class.java,
            OverHealPerk::class.java,
            SafetyFirstPerk::class.java,
            SafetySecondPerk::class.java,
            StrengthPerk::class.java,
            TrickleDownPerk::class.java,
            VampirePerk::class.java,
            BeastModeMegaStreak::class.java,
            RAndRKillStreak::class.java,
            TacticalRetreatKillStreak::class.java,
            ToughSkinKillStreak::class.java,
            ApostleForTheGesusKillStreak::class.java,
            AssuredStrikeKillStreak::class.java,
            GrandFinaleMegaStreak::class.java,
            LeechKillStreak::class.java,
            AuraOfProtectionKillStreak::class.java,
            GlassPickaxeKillStreak::class.java,
            HermitMegaStreak::class.java,
            IceCubeKillStreak::class.java,
            PungentKillStreak::class.java,
            GoldNanoFactoryKillStreak::class.java,
            HighlanderMegaStreak::class.java,
            KhanateKillStreak::class.java,
            WitherCraftKillStreak::class.java,
            ArquebusierKillStreak::class.java,
            ExpliciousKillStreak::class.java,
            FightOrFlightKillStreak::class.java,
            OverDriveMegaStreak::class.java,
            SecondGappleKillStreak::class.java,
            SpongeSteveKillStreak::class.java,
            UberStreak::class.java,
            ToTheMoonMegaStreak::class.java,
            SuperStreaker::class.java
        )
        perkFactory.init(classes)
    }

    private fun registerSounds() {
        listOf(
            DoubleStreakSound,
            TripleStreakSound,
            QuadraStreakSound,
            StreakSound,
            SuccessfullySound
        ).forEach {
            ThePit.getInstance().soundFactory.registerSound(it)
        }
    }

    private fun loadNpcs() {
        val npc = ThePit.getInstance().npcFactory
        Bukkit.getServer().pluginManager.registerEvents(npc, ThePit.getInstance())
        npc.init(
            listOf(
                GenesisAngelNpc::class.java,
                GenesisDemonNpc::class.java,
                KeeperNPC::class.java,
                MailNpc::class.java,
                PerkNPC::class.java,
                PrestigeNPC::class.java,
                QuestNpc::class.java,
                ShopNPC::class.java,
                StatusNPC::class.java
            )
        )
    }

    private fun loadQuests() {
        val questFactory = ThePit.getInstance().questFactory
        val classes = listOf<Class<*>>(
            DeepInfiltration::class.java,
            DestoryArmor::class.java,
            DryBlood::class.java,
            HighValueTarget::class.java,
            KeepSilence::class.java,
            LowEfficiency::class.java,
            LowHealth::class.java,
            SinkingMoonlight::class.java
        )
        questFactory.init(classes)
    }

    private fun loadEvents() {
        val eventFactory = ThePit.getInstance().eventFactory
        val classes = listOf<Class<*>>(
            HamburgerEvent::class.java,
            RagePitEvent::class.java,
            RedVSBlueEvent::class.java,
            BlockHeadEvent::class.java,
//            SpireEvent::class.java,
            AuctionEvent::class.java,
            CakeEvent::class.java,
            CarePackageEvent::class.java,
            EveOneBountyEvent::class.java,
            QuickMathEvent::class.java,
            SquadsEvent::class.java,
            DoubleRewardsEvent::class.java,
            RespawnFamilyEvent::class.java
        )

        eventFactory.init(classes)
    }

    private fun registerListeners() {
        val classes = listOf<Class<*>>(
            CombatListener::class.java,
            GameEffectListener::class.java,
            DataListener::class.java,
            EnderChestListener::class.java,
            ChatListener::class.java,
            PlayerListener::class.java,
            ProtectListener::class.java,
            PantsBundleShopButton::class.java,
            SwordBundleShopButton::class.java,
            BowBundleShopButton::class.java,
            CombatSpadeShopButton::class.java,
            MailSendListener::class.java,
            SafetyJoinListener::class.java,
            ButtonListener::class.java,
            GenesisCombatListener::class.java,
            FixListeners::class.java,
            TradeListener::class.java,
            HologramListener::class.java,
            TickHandler::class.java,
            AsyncTickHandler::class.java
        )
        for (aClass in classes) {
            try {
                val o = aClass.newInstance()
                Bukkit.getPluginManager().registerEvents(o as Listener, ThePit.getInstance())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        CarbonSpigot.getPacketAPI().registerPacketHandler(ThePit.getInstance(),PacketListener())

        if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            Bukkit.getPluginManager().registerEvents(MythicMobListener, ThePit.getInstance());
        }
    }

}