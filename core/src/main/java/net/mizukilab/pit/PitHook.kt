package net.mizukilab.pit

import cn.charlotte.pit.ThePit
import net.mizukilab.pit.actionbar.ActionBarManager
import net.mizukilab.pit.command.PitAdminCommands
import net.mizukilab.pit.command.PitAdminDupeFixCommands
import net.mizukilab.pit.command.PitAdminSimpleCommand
import net.mizukilab.pit.command.PitCommands
import net.mizukilab.pit.command.handler.HandHasItem
import net.mizukilab.pit.command.handler.HandHasItemValidator
import net.mizukilab.pit.command.handler.metaKey
import net.mizukilab.pit.config.NewConfiguration
import cn.charlotte.pit.data.CDKData
import net.mizukilab.pit.data.operator.ProfileOperator
import net.mizukilab.pit.enchantment.AbstractEnchantment
import net.mizukilab.pit.enchantment.type.aqua.ClubRodEnchant
import net.mizukilab.pit.enchantment.type.aqua.GrandmasterEnchant
import net.mizukilab.pit.enchantment.type.aqua.LuckOfPondEnchant
import net.mizukilab.pit.enchantment.type.aqua.RogueEnchant
import net.mizukilab.pit.enchantment.type.dark_rare.ComboDazzlingGoldEnchant
import net.mizukilab.pit.enchantment.type.dark_rare.ComboUnpredictablyEnchant
import net.mizukilab.pit.enchantment.type.dark_rare.ComboVenomEnchant
import net.mizukilab.pit.enchantment.type.dark_rare.GoldenHandcuffsEnchant
import net.mizukilab.pit.enchantment.type.ragerare.Regularity
import net.mizukilab.pit.enchantment.type.ragerare.ThinkOfThePeopleEnchant
import net.mizukilab.pit.enchantment.type.sewer_normal.AegisEnchant
import net.mizukilab.pit.enchantment.type.special.SoulRipperEnchant
import cn.charlotte.pit.events.genesis.listener.GenesisCombatListener
import net.mizukilab.pit.hologram.HologramListener
import net.mizukilab.pit.hook.PitPapiHook
import net.mizukilab.pit.item.AbstractPitItem
import net.mizukilab.pit.item.ItemFactory
import net.mizukilab.pit.map.kingsquests.KingsQuests
import net.mizukilab.pit.menu.shop.button.type.BowBundleShopButton
import net.mizukilab.pit.menu.shop.button.type.CombatSpadeShopButton
import net.mizukilab.pit.menu.shop.button.type.PantsBundleShopButton
import net.mizukilab.pit.menu.shop.button.type.SwordBundleShopButton
import net.mizukilab.pit.menu.trade.TradeListener
import net.mizukilab.pit.nametag.NameTagImpl
import cn.charlotte.pit.perk.AbstractPerk
import net.mizukilab.pit.perk.type.streak.beastmode.BeastModeMegaStreak
import net.mizukilab.pit.perk.type.streak.beastmode.RAndRKillStreak
import net.mizukilab.pit.perk.type.streak.beastmode.TacticalRetreatKillStreak
import net.mizukilab.pit.perk.type.streak.beastmode.ToughSkinKillStreak
import net.mizukilab.pit.perk.type.streak.grandfinale.ApostleForTheGesusKillStreak
import net.mizukilab.pit.perk.type.streak.grandfinale.AssuredStrikeKillStreak
import net.mizukilab.pit.perk.type.streak.grandfinale.GrandFinaleMegaStreak
import net.mizukilab.pit.perk.type.streak.grandfinale.LeechKillStreak
import net.mizukilab.pit.perk.type.streak.highlander.GoldNanoFactoryKillStreak
import net.mizukilab.pit.perk.type.streak.highlander.HighlanderMegaStreak
import net.mizukilab.pit.perk.type.streak.highlander.KhanateKillStreak
import net.mizukilab.pit.perk.type.streak.highlander.WitherCraftKillStreak
import net.mizukilab.pit.perk.type.streak.tothemoon.SuperStreaker
import net.mizukilab.pit.perk.type.streak.tothemoon.ToTheMoonMegaStreak
import net.mizukilab.pit.perk.type.streak.uber.UberStreak
import net.mizukilab.pit.scoreboard.Scoreboard
import cn.charlotte.pit.util.hologram.packet.PacketHologramRunnable
import net.mizukilab.pit.util.menu.ButtonListener
import net.mizukilab.pit.util.nametag.NametagHandler
import net.mizukilab.pit.util.scoreboard.Assemble
import com.comphenix.protocol.ProtocolLibrary
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory
import dev.rollczi.litecommands.bukkit.LiteBukkitMessages
import dev.rollczi.litecommands.meta.Meta
import dev.rollczi.litecommands.validator.ValidatorScope
import net.mizukilab.pit.enchantment.type.dark_normal.GrimReaperEnchant
import net.mizukilab.pit.enchantment.type.dark_normal.HedgeFundEnchant
import net.mizukilab.pit.enchantment.type.dark_normal.MindAssaultEnchant
import net.mizukilab.pit.enchantment.type.dark_normal.MiseryEnchant
import net.mizukilab.pit.enchantment.type.dark_normal.SanguisugeEnchant
import net.mizukilab.pit.enchantment.type.dark_normal.SomberEnchant
import net.mizukilab.pit.enchantment.type.dark_normal.SpiteEnchant
import net.mizukilab.pit.enchantment.type.genesis.EvilWithinEnchant
import net.mizukilab.pit.enchantment.type.genesis.GuardianEnchant
import net.mizukilab.pit.enchantment.type.genesis.JerryEnchant
import net.mizukilab.pit.enchantment.type.genesis.JerryEnchant2
import net.mizukilab.pit.enchantment.type.genesis.JerryEnchant3
import net.mizukilab.pit.enchantment.type.genesis.JerryEnchant4
import net.mizukilab.pit.enchantment.type.genesis.JerryEnchant5
import net.mizukilab.pit.enchantment.type.genesis.JerryEnchant6
import net.mizukilab.pit.enchantment.type.genesis.JerryEnchant7
import net.mizukilab.pit.enchantment.type.normal.AntiAbsorptionEnchant
import net.mizukilab.pit.enchantment.type.normal.AntiBowSpammerEnchantP
import net.mizukilab.pit.enchantment.type.normal.AntiBowSpammerEnchantW
import net.mizukilab.pit.enchantment.type.normal.AntiMythicismEnchant
import net.mizukilab.pit.enchantment.type.normal.ArrowArmoryEnchant
import net.mizukilab.pit.enchantment.type.normal.BerserkerEnchant
import net.mizukilab.pit.enchantment.type.normal.BillyEnchant
import net.mizukilab.pit.enchantment.type.normal.BooBooEnchant
import net.mizukilab.pit.enchantment.type.normal.BountyHunterEnchant
import net.mizukilab.pit.enchantment.type.normal.BowComboEnchant
import net.mizukilab.pit.enchantment.type.normal.BruiserEnchant
import net.mizukilab.pit.enchantment.type.normal.BulletTimeEnchant
import net.mizukilab.pit.enchantment.type.normal.ComboDamageEnchant
import net.mizukilab.pit.enchantment.type.normal.ComboHealEnchant
import net.mizukilab.pit.enchantment.type.normal.ComboSwiftEnchant
import net.mizukilab.pit.enchantment.type.normal.CounterJanitorEnchant
import net.mizukilab.pit.enchantment.type.normal.CounterOffensiveEnchant
import net.mizukilab.pit.enchantment.type.normal.CreativeEnchant
import net.mizukilab.pit.enchantment.type.normal.CriticallyFunkyEnchant
import net.mizukilab.pit.enchantment.type.normal.CriticallyRichEnchant
import net.mizukilab.pit.enchantment.type.normal.CrushEnchant
import net.mizukilab.pit.enchantment.type.normal.DavidAndGoliathEnchant
import net.mizukilab.pit.enchantment.type.normal.DiamondAllergyEnchant
import net.mizukilab.pit.enchantment.type.normal.DiamondBreakerEnchant
import net.mizukilab.pit.enchantment.type.normal.ElectrolytesEnchant
import net.mizukilab.pit.enchantment.type.normal.EndlessQuiverEnchant
import net.mizukilab.pit.enchantment.type.normal.FractionalReserveEnchant
import net.mizukilab.pit.enchantment.type.normal.GoldExplorerEnchant
import net.mizukilab.pit.enchantment.type.normal.GutsEnchant
import net.mizukilab.pit.enchantment.type.normal.HermesEnchant
import net.mizukilab.pit.enchantment.type.normal.HuntTheHunterEnchant
import net.mizukilab.pit.enchantment.type.normal.KingKillersEnchant
import net.mizukilab.pit.enchantment.type.normal.LifeStealEnchant
import net.mizukilab.pit.enchantment.type.normal.LureEnchant
import net.mizukilab.pit.enchantment.type.normal.MirrorEnchant
import net.mizukilab.pit.enchantment.type.normal.MixedCombatEnchant
import net.mizukilab.pit.enchantment.type.normal.NotGladiatorEnchant
import net.mizukilab.pit.enchantment.type.normal.OverHealEnchant
import net.mizukilab.pit.enchantment.type.normal.PantsRadarEnchant
import net.mizukilab.pit.enchantment.type.normal.ParasiteEnchant
import net.mizukilab.pit.enchantment.type.normal.PebbleEnchant
import net.mizukilab.pit.enchantment.type.normal.PeroxideEnchant
import net.mizukilab.pit.enchantment.type.normal.PhantomShieldEnchant
import net.mizukilab.pit.enchantment.type.normal.PitMBAEnchant
import net.mizukilab.pit.enchantment.type.normal.PitPocketEnchant
import net.mizukilab.pit.enchantment.type.normal.PowerEnchant
import net.mizukilab.pit.enchantment.type.normal.ProtectionEnchant
import net.mizukilab.pit.enchantment.type.normal.PurpleGoldEnchant
import net.mizukilab.pit.enchantment.type.normal.ReaperEnchant
import net.mizukilab.pit.enchantment.type.normal.ResentmentEnchant
import net.mizukilab.pit.enchantment.type.normal.RespawnAbsorptionEnchant
import net.mizukilab.pit.enchantment.type.normal.RespawnResistanceEnchant
import net.mizukilab.pit.enchantment.type.normal.RustBowEnchant
import net.mizukilab.pit.enchantment.type.normal.SelfCheckoutEnchant
import net.mizukilab.pit.enchantment.type.normal.SharkEnchant
import net.mizukilab.pit.enchantment.type.normal.SharpnessEnchant
import net.mizukilab.pit.enchantment.type.normal.SierraEnchant
import net.mizukilab.pit.enchantment.type.normal.SniperEnchant
import net.mizukilab.pit.enchantment.type.normal.SpeedyKillEnchant
import net.mizukilab.pit.enchantment.type.normal.SprintDrainEnchant
import net.mizukilab.pit.enchantment.type.normal.StrikeGoldEnchant
import net.mizukilab.pit.enchantment.type.normal.TNTEnchant
import net.mizukilab.pit.enchantment.type.normal.ThornsEnchant
import net.mizukilab.pit.enchantment.type.normal.ThumpEnchant
import net.mizukilab.pit.enchantment.type.normal.TrotEnchant
import net.mizukilab.pit.enchantment.type.normal.TrueDamageArrowEnchant
import net.mizukilab.pit.enchantment.type.normal.UnBreakEnchant
import net.mizukilab.pit.enchantment.type.normal.WaspEnchant
import net.mizukilab.pit.enchantment.type.normal.WisdomEnchant
import net.mizukilab.pit.enchantment.type.op.BlazingAngelEnchant
import net.mizukilab.pit.enchantment.type.op.BounceBowEnchant
import net.mizukilab.pit.enchantment.type.op.DJBundlePVZ
import net.mizukilab.pit.enchantment.type.op.EchoOfSnowlandPEnchant
import net.mizukilab.pit.enchantment.type.op.EchoOfSnowlandWEnchant
import net.mizukilab.pit.enchantment.type.op.EmergencyColonyEnchant
import net.mizukilab.pit.enchantment.type.op.KFCBoomerEnchant
import net.mizukilab.pit.enchantment.type.op.LaserEnchant
import net.mizukilab.pit.enchantment.type.op.MultiExchangeLocationEnchant
import net.mizukilab.pit.enchantment.type.op.OPDamageEnchant
import net.mizukilab.pit.enchantment.type.op.PowerAngelEnchant
import net.mizukilab.pit.enchantment.type.op.StarJudgementEnchant
import net.mizukilab.pit.enchantment.type.op.SuperLaserEnchant
import net.mizukilab.pit.enchantment.type.op.SuperSlimeEnchant
import net.mizukilab.pit.enchantment.type.op.TestEnchant
import net.mizukilab.pit.enchantment.type.op.VerminEnchant
import net.mizukilab.pit.enchantment.type.rage.AceOfSpades
import net.mizukilab.pit.enchantment.type.rage.Brakes
import net.mizukilab.pit.enchantment.type.rage.BreachingChargeEnchant
import net.mizukilab.pit.enchantment.type.rage.NewDealEnchant
import net.mizukilab.pit.enchantment.type.rage.ReallyToxicEnchant
import net.mizukilab.pit.enchantment.type.rage.SingularityEnchant
import net.mizukilab.pit.enchantment.type.rare.AbsorptionEnchant
import net.mizukilab.pit.enchantment.type.rare.ArchangelEnchant
import net.mizukilab.pit.enchantment.type.rare.AssassinEnchant
import net.mizukilab.pit.enchantment.type.rare.BillionaireEnchant
import net.mizukilab.pit.enchantment.type.rare.ComboStrikeEnchant
import net.mizukilab.pit.enchantment.type.rare.ComboStunEnchant
import net.mizukilab.pit.enchantment.type.rare.DivineMiracleEnchant
import net.mizukilab.pit.enchantment.type.rare.EnderBowEnchant
import net.mizukilab.pit.enchantment.type.rare.ExecutionerEnchant
import net.mizukilab.pit.enchantment.type.rare.FightOrDieEnchant
import net.mizukilab.pit.enchantment.type.rare.GambleEnchant
import net.mizukilab.pit.enchantment.type.rare.GomrawsHeartEnchant
import net.mizukilab.pit.enchantment.type.rare.HealShieldEnchant
import net.mizukilab.pit.enchantment.type.rare.HealerEnchant
import net.mizukilab.pit.enchantment.type.rare.HemorrhageEnchant
import net.mizukilab.pit.enchantment.type.rare.LuckyShotEnchant
import net.mizukilab.pit.enchantment.type.rare.MegaLongBowEnchant
import net.mizukilab.pit.enchantment.type.rare.MysticRealmEnchant
import net.mizukilab.pit.enchantment.type.rare.NightFallEnchant
import net.mizukilab.pit.enchantment.type.rare.PaparazziEnchant
import net.mizukilab.pit.enchantment.type.rare.PullBowEnchant
import net.mizukilab.pit.enchantment.type.rare.SlimeEnchant
import net.mizukilab.pit.enchantment.type.rare.SnowballsEnchant
import net.mizukilab.pit.enchantment.type.rare.SolitudeEnchant
import net.mizukilab.pit.enchantment.type.rare.SoulEarterEnchant
import net.mizukilab.pit.enchantment.type.rare.SpeedyHitEnchant
import net.mizukilab.pit.enchantment.type.rare.ThePunchEnchant
import net.mizukilab.pit.enchantment.type.rare.TheSwiftWindEnchant
import net.mizukilab.pit.enchantment.type.rare.ThunderArrowEnchant
import net.mizukilab.pit.enchantment.type.rare.UndeadArrowEnchant
import net.mizukilab.pit.enchantment.type.rare.VolleyEnchant
import net.mizukilab.pit.events.impl.AuctionEvent
import net.mizukilab.pit.events.impl.CakeEvent
import net.mizukilab.pit.events.impl.CarePackageEvent
import net.mizukilab.pit.events.impl.DragonEggsEvent
import net.mizukilab.pit.events.impl.EveOneBountyEvent
import net.mizukilab.pit.events.impl.HuntEvent
import net.mizukilab.pit.events.impl.QuickMathEvent
import net.mizukilab.pit.events.impl.major.BlockHeadEvent
import net.mizukilab.pit.events.impl.major.HamburgerEvent
import net.mizukilab.pit.events.impl.major.RagePitEvent
import net.mizukilab.pit.events.impl.major.RedVSBlueEvent
import net.mizukilab.pit.events.impl.major.RespawnFamilyEvent
import net.mizukilab.pit.events.impl.major.SquadsEvent
import net.mizukilab.pit.item.type.AngelChestplate
import net.mizukilab.pit.item.type.ArmageddonBoots
import net.mizukilab.pit.item.type.BountySolventPotion
import net.mizukilab.pit.item.type.ChunkOfVileItem
import net.mizukilab.pit.item.type.FunkyFeather
import net.mizukilab.pit.item.type.GlobalAttentionGem
import net.mizukilab.pit.item.type.GoldenHelmet
import net.mizukilab.pit.item.type.JewelSword
import net.mizukilab.pit.item.type.JumpBoostPotion
import net.mizukilab.pit.item.type.MythicEnchantingTable
import net.mizukilab.pit.item.type.MythicRepairKit
import net.mizukilab.pit.item.type.PitCactus
import net.mizukilab.pit.item.type.SpireArmor
import net.mizukilab.pit.item.type.SuperPackage
import net.mizukilab.pit.item.type.TotallyLegitGem
import net.mizukilab.pit.item.type.UberDrop
import net.mizukilab.pit.listener.ChatListener
import net.mizukilab.pit.listener.CombatListener
import net.mizukilab.pit.listener.DataListener
import net.mizukilab.pit.listener.EnderChestListener
import net.mizukilab.pit.listener.GameEffectListener
import net.mizukilab.pit.listener.MailSendListener
import net.mizukilab.pit.listener.MythicMobListener
import net.mizukilab.pit.listener.PacketListener
import net.mizukilab.pit.listener.PlayerListener
import net.mizukilab.pit.listener.ProtectListener
import net.mizukilab.pit.listener.SafetyJoinListener
import net.mizukilab.pit.npc.type.GenesisAngelNpc
import net.mizukilab.pit.npc.type.GenesisDemonNpc
import net.mizukilab.pit.npc.type.KeeperNPC
import net.mizukilab.pit.npc.type.MailNpc
import net.mizukilab.pit.npc.type.PerkNPC
import net.mizukilab.pit.npc.type.PrestigeNPC
import net.mizukilab.pit.npc.type.QuestNpc
import net.mizukilab.pit.npc.type.ShopNPC
import net.mizukilab.pit.npc.type.StatusNPC
import net.mizukilab.pit.perk.type.boost.BowBoostPerk
import net.mizukilab.pit.perk.type.boost.BuildBattlerBoostPerk
import net.mizukilab.pit.perk.type.boost.CoinBoostPerk
import net.mizukilab.pit.perk.type.boost.CoinContractBoostPerk
import net.mizukilab.pit.perk.type.boost.CoinPrestigeBoostPerk
import net.mizukilab.pit.perk.type.boost.DmgReduceBoostPerk
import net.mizukilab.pit.perk.type.boost.ElGatoBoostPerk
import net.mizukilab.pit.perk.type.boost.MeleeBoostPerk
import net.mizukilab.pit.perk.type.boost.XPBoostPerk
import net.mizukilab.pit.perk.type.boost.XPContractBoostPerk
import net.mizukilab.pit.perk.type.boost.XPPrestigeBoostPerk
import net.mizukilab.pit.perk.type.prestige.ArrowArmoryPerk
import net.mizukilab.pit.perk.type.prestige.AssistantToTheStreakerPerk
import net.mizukilab.pit.perk.type.prestige.AutoBuyPerk
import net.mizukilab.pit.perk.type.prestige.BarbarianPerk
import net.mizukilab.pit.perk.type.prestige.BeastModeBundlePerk
import net.mizukilab.pit.perk.type.prestige.BountySolventShopPerk
import net.mizukilab.pit.perk.type.prestige.BowBundleShopPerk
import net.mizukilab.pit.perk.type.prestige.CelebrityPerk
import net.mizukilab.pit.perk.type.prestige.CombatSpadePerk
import net.mizukilab.pit.perk.type.prestige.ContractorPerk
import net.mizukilab.pit.perk.type.prestige.CoolPerk
import net.mizukilab.pit.perk.type.prestige.CoopCatPerk
import net.mizukilab.pit.perk.type.prestige.DiamondLeggingsShopPerk
import net.mizukilab.pit.perk.type.prestige.DirtyPerk
import net.mizukilab.pit.perk.type.prestige.DivineInterventionPerk
import net.mizukilab.pit.perk.type.prestige.ExtraEnderchestPerk
import net.mizukilab.pit.perk.type.prestige.ExtraHeartPerk
import net.mizukilab.pit.perk.type.prestige.ExtraKillStreakSlotPerk
import net.mizukilab.pit.perk.type.prestige.ExtraPerkSlotPerk
import net.mizukilab.pit.perk.type.prestige.FastPassPerk
import net.mizukilab.pit.perk.type.prestige.FirstAidEggPerk
import net.mizukilab.pit.perk.type.prestige.FirstStrikePerk
import net.mizukilab.pit.perk.type.prestige.FishClubPerk
import net.mizukilab.pit.perk.type.prestige.GoldPickaxePerk
import net.mizukilab.pit.perk.type.prestige.GrandFinaleBundlePerk
import net.mizukilab.pit.perk.type.prestige.HeresyPerk
import net.mizukilab.pit.perk.type.prestige.HermitBundlePerk
import net.mizukilab.pit.perk.type.prestige.HighlanderBundlePerk
import net.mizukilab.pit.perk.type.prestige.ImpatientPerk
import net.mizukilab.pit.perk.type.prestige.IronPackShopPerk
import net.mizukilab.pit.perk.type.prestige.JumpBoostShopPerk
import net.mizukilab.pit.perk.type.prestige.KungFuKnowledgePerk
import net.mizukilab.pit.perk.type.prestige.MarathonPerk
import net.mizukilab.pit.perk.type.prestige.MonsterPerk
import net.mizukilab.pit.perk.type.prestige.MythicismPerk
import net.mizukilab.pit.perk.type.prestige.ObsidianStackShopPerk
import net.mizukilab.pit.perk.type.prestige.OlympusPerk
import net.mizukilab.pit.perk.type.prestige.PantsBundleShopPerk
import net.mizukilab.pit.perk.type.prestige.PromotionPerk
import net.mizukilab.pit.perk.type.prestige.PureRage
import net.mizukilab.pit.perk.type.prestige.RamboPerk
import net.mizukilab.pit.perk.type.prestige.RawNumbersPerk
import net.mizukilab.pit.perk.type.prestige.ReconPerk
import net.mizukilab.pit.perk.type.prestige.ScamArtistPerk
import net.mizukilab.pit.perk.type.prestige.SelfConfidencePerk
import net.mizukilab.pit.perk.type.prestige.SwrodBundleShopPerk
import net.mizukilab.pit.perk.type.prestige.TacticalInsertionsPerk
import net.mizukilab.pit.perk.type.prestige.TastySoupPerk
import net.mizukilab.pit.perk.type.prestige.TenacityPerk
import net.mizukilab.pit.perk.type.prestige.TheWayPerk
import net.mizukilab.pit.perk.type.prestige.ThickPerk
import net.mizukilab.pit.perk.type.prestige.ToTheMoonBundle
import net.mizukilab.pit.perk.type.prestige.YummyPerk
import net.mizukilab.pit.perk.type.shop.ArrowRecoveryPerk
import net.mizukilab.pit.perk.type.shop.BountyHunterPerk
import net.mizukilab.pit.perk.type.shop.FishingRodPerk
import net.mizukilab.pit.perk.type.shop.GladiatorPerk
import net.mizukilab.pit.perk.type.shop.GoldMinerPerk
import net.mizukilab.pit.perk.type.shop.GoldenHeadPerk
import net.mizukilab.pit.perk.type.shop.LuckyDiamondPerk
import net.mizukilab.pit.perk.type.shop.MinerPerk
import net.mizukilab.pit.perk.type.shop.OverHealPerk
import net.mizukilab.pit.perk.type.shop.SafetyFirstPerk
import net.mizukilab.pit.perk.type.shop.SafetySecondPerk
import net.mizukilab.pit.perk.type.shop.StrengthPerk
import net.mizukilab.pit.perk.type.shop.TrickleDownPerk
import net.mizukilab.pit.perk.type.shop.VampirePerk
import net.mizukilab.pit.perk.type.streak.hermit.AuraOfProtectionKillStreak
import net.mizukilab.pit.perk.type.streak.hermit.GlassPickaxeKillStreak
import net.mizukilab.pit.perk.type.streak.hermit.HermitMegaStreak
import net.mizukilab.pit.perk.type.streak.hermit.IceCubeKillStreak
import net.mizukilab.pit.perk.type.streak.hermit.PungentKillStreak
import net.mizukilab.pit.perk.type.streak.nonpurchased.ArquebusierKillStreak
import net.mizukilab.pit.perk.type.streak.nonpurchased.ExpliciousKillStreak
import net.mizukilab.pit.perk.type.streak.nonpurchased.FightOrFlightKillStreak
import net.mizukilab.pit.perk.type.streak.nonpurchased.OverDriveMegaStreak
import net.mizukilab.pit.perk.type.streak.nonpurchased.SecondGappleKillStreak
import net.mizukilab.pit.perk.type.streak.nonpurchased.SpongeSteveKillStreak
import net.mizukilab.pit.quest.type.DeepInfiltration
import net.mizukilab.pit.quest.type.DestoryArmor
import net.mizukilab.pit.quest.type.DryBlood
import net.mizukilab.pit.quest.type.HighValueTarget
import net.mizukilab.pit.quest.type.KeepSilence
import net.mizukilab.pit.quest.type.LowEfficiency
import net.mizukilab.pit.quest.type.LowHealth
import net.mizukilab.pit.quest.type.SinkingMoonlight
import net.mizukilab.pit.runnable.ActionBarDisplayRunnable
import net.mizukilab.pit.runnable.AsyncTickHandler
import net.mizukilab.pit.runnable.BountyRunnable
import net.mizukilab.pit.runnable.FreeExpRunnable
import net.mizukilab.pit.runnable.GoldDropRunnable
import net.mizukilab.pit.runnable.NightVisionRunnable
import net.mizukilab.pit.runnable.ProtectRunnable
import net.mizukilab.pit.runnable.SewersRunnable
import net.mizukilab.pit.runnable.TickHandler
import net.mizukilab.pit.sound.impl.DoubleStreakSound
import net.mizukilab.pit.sound.impl.QuadraStreakSound
import net.mizukilab.pit.sound.impl.StreakSound
import net.mizukilab.pit.sound.impl.SuccessfullySound
import net.mizukilab.pit.sound.impl.TripleStreakSound
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginDescriptionFile
import real.nanoneko.EnchantedConstructor
import real.nanoneko.ItemConstructor
import real.nanoneko.PerkConstructor

object PitHook {
    @JvmStatic
    val gitVersion = "53c934dac"

    @JvmStatic
    val itemVersion = "p_uuid"
    fun init() {
        try {
            NewConfiguration.loadFile()
            NewConfiguration.load()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        loadOperator()
        loadItemFactory()
        loadActionBar()
        loadEnchants()
        filter()
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

        ActionBarDisplayRunnable.start()

        KingsQuests.enable()

        CDKData.loadAllCDKFromData()

        Bukkit.getPluginManager().registerEvents(SewersRunnable, ThePit.getInstance())
        SewersRunnable.runTaskTimer(ThePit.getInstance(), 20L, 20L)
        //CleanupDupeEnch0525Runnable.runTaskTimer(ThePit.getInstance(), 20L, 20L)
        //SpecialPlayerRunnable.runTaskTimer(ThePit.getInstance(), 1L, 1L)
        //PrivatePlayerRunnable.runTaskTimer(ThePit.getInstance(),1L,1L)
    }

    private fun filter() {
        NewConfiguration.forbidEnchant.forEach { i ->
            val toString = i
            ThePit.getInstance().sendLogs("Unregistering $toString");
            ThePit.getInstance().enchantmentFactor.unregister(null, toString);
        }
    }

    private fun loadActionBar() {
        ThePit.getInstance().actionBarManager = ActionBarManager();
    }

    private fun loadItemFactory() {
        ThePit.getInstance().itemFactory = ItemFactory();
    }

    private fun loadOperator() {
        ThePit.getInstance().profileOperator = ProfileOperator(ThePit.getInstance());
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


        //AnnouncementRunnable.runTaskTimerAsynchronously(ThePit.getInstance(), 0, 40 * 60)
        TickHandler().runTaskTimer(ThePit.getInstance(), 1, 1)
        AsyncTickHandler().runTaskTimerAsynchronously(ThePit.getInstance(), 1, 1)
        GoldDropRunnable().runTaskTimer(ThePit.getInstance(), 20, 20)

        ProtectRunnable().runTaskTimer(ThePit.getInstance(), 20, 20)

        FreeExpRunnable().runTaskTimer(ThePit.getInstance(), 20 * 60 * 15, 20 * 60 * 15)
        NightVisionRunnable().runTaskTimer(ThePit.getInstance(), 20, 20)

        PacketHologramRunnable().runTaskTimerAsynchronously(ThePit.getInstance(), 20, 20)

        BountyRunnable().runTaskTimerAsynchronously(ThePit.getInstance(), 5, NewConfiguration.bountyTickInterval.toLong())
    }

    private fun loadItems() {
        val clazzList = mutableListOf(
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
        )

        try {
            if (ItemConstructor.getItems().isNotEmpty()) {
                println("加载额外物品中...")
                clazzList.addAll(ItemConstructor.getItems())
            }
            for (clazz in clazzList) {
                try {
                    val pitItem = clazz.getDeclaredConstructor().newInstance()
                    if (pitItem is AbstractPitItem) {
                        if (pitItem is Listener) {
                            Bukkit.getPluginManager().registerEvents(pitItem, ThePit.getInstance())
                        }
                        ThePit.getInstance().itemFactor.registerItem(pitItem)
                    }
                } catch (e: Exception) {
                    println("An error occurred: ${e.message}")
                }
            }
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }
}

private fun loadEnchants() {
    val enchantmentFactor = ThePit.getInstance().enchantmentFactor
    val classes = mutableListOf(
        //new
        ThunderArrowEnchant::class.java,
        UndeadArrowEnchant::class.java,
        //end
        ComboUnpredictablyEnchant::class.java,
        ComboDazzlingGoldEnchant::class.java,
        NightFallEnchant::class.java,
        MysticRealmEnchant::class.java,
        TheSwiftWindEnchant::class.java,
        SoulEarterEnchant::class.java,
        PhantomShieldEnchant::class.java,
        KingKillersEnchant::class.java,
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
        JerryEnchant6::class.java,
        JerryEnchant7::class.java,
        AntiAbsorptionEnchant::class.java,
        AntiBowSpammerEnchantP::class.java,
        AntiBowSpammerEnchantW::class.java,
        AntiMythicismEnchant::class.java,
        ArrowArmoryEnchant::class.java,
        BerserkerEnchant::class.java,
        BillyEnchant::class.java,
        BooBooEnchant::class.java,
        BountyHunterEnchant::class.java,
        BowComboEnchant::class.java,
        BruiserEnchant::class.java,
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
        VerminEnchant::class.java,
        PowerAngelEnchant::class.java,
        StarJudgementEnchant::class.java,
        SuperLaserEnchant::class.java,
        SuperSlimeEnchant::class.java,
        TestEnchant::class.java,
        AbsorptionEnchant::class.java,
        ArchangelEnchant::class.java,
        AssassinEnchant::class.java,
        BillionaireEnchant::class.java,
        ComboStrikeEnchant::class.java,
        ComboStunEnchant::class.java,
        DivineMiracleEnchant::class.java,
        EnderBowEnchant::class.java,
        ExecutionerEnchant::class.java,
        FightOrDieEnchant::class.java,
        GambleEnchant::class.java,
        GomrawsHeartEnchant::class.java,
        HealerEnchant::class.java,
        HealShieldEnchant::class.java,
        HemorrhageEnchant::class.java,
        LuckyShotEnchant::class.java,
        MegaLongBowEnchant::class.java,
        PaparazziEnchant::class.java,
        PullBowEnchant::class.java,
        SlimeEnchant::class.java,
        SnowballsEnchant::class.java,
        SolitudeEnchant::class.java,
        SpeedyHitEnchant::class.java,
        ThePunchEnchant::class.java,
        VolleyEnchant::class.java,
        AegisEnchant::class.java,
        SoulRipperEnchant::class.java,
        AceOfSpades::class.java,
        Brakes::class.java,
        BreachingChargeEnchant::class.java,
        TrotEnchant::class.java
    )

    /*        classes += HappyNewYearEnchant::class.java
            classes += WitheredAndPiercingThroughTheHeart::class.java
            classes += LastShadowLeapForward::class.java
*/
//        classes += TrotEnchant::class.java
//        classes += TrytoGiveEnchant::class.java
//
//        classes += BreakArmorEnchant::class.java
//        //classes += CoinGloriousEnchant::class.java
//        classes += ComboBacktrackEnchant::class.java
//        classes += DoubleJumpEnchant::class.java
//        classes += SacredArrowEnchant::class.java
//        //classes += TrashPandaEnchant::class.java
//        classes += JerryEnchant5::class.java

    //  classes += Limit24520Ench::class.java
    //  classes += LimitXZQ1Ench::class.java
    val enchantmentClasses: List<Class<*>> = EnchantedConstructor.getEnchantments()
    val enchantmentCollection: List<Class<out AbstractEnchantment>> =
        enchantmentClasses.filterIsInstance<Class<out AbstractEnchantment>>()
    if (enchantmentCollection.isNotEmpty()) {
        println("加载额外附魔中...")
        classes.addAll(enchantmentCollection)
    }

    enchantmentFactor.init(classes)

}

private fun loadScoreBoard() {
    val assemble = Assemble(ThePit.getInstance(), Scoreboard())
    assemble.ticks = 1
}

private fun loadNameTag() {
    val nametagHandler = NametagHandler(ThePit.getInstance(), NameTagImpl())
    nametagHandler.ticks = 20
}

private fun loadPerks() {
    val perkFactory = ThePit.getInstance().perkFactory
    val classes = mutableListOf(
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
        KungFuKnowledgePerk::class.java,
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
        //   MonsterKillStreak::class.java,
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

    val perkClasses: List<Class<*>> = PerkConstructor.getPerks()
    val perkCollection: List<Class<out AbstractPerk>> =
        perkClasses.filterIsInstance<Class<out AbstractPerk>>()


    if (perkCollection.isNotEmpty()) {
        println("加载额外增益中...")
        classes.addAll(perkCollection)
    }
    perkFactory.init(classes as Collection<Class<*>>?)
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
            StatusNPC::class.java,
        )
    )
    println("load Npc...")
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
        DragonEggsEvent::class.java,
        HuntEvent::class.java,

        CarePackageEvent::class.java,
        EveOneBountyEvent::class.java,
        QuickMathEvent::class.java,
        SquadsEvent::class.java,
        /*        DoubleRewardsEvent::class.java,*/
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
        TradeListener::class.java,
        HologramListener::class.java,
    )
    for (aClass in classes) {
        try {
            val o = aClass.getConstructor().newInstance()
            Bukkit.getPluginManager().registerEvents(o as Listener, ThePit.getInstance())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    ProtocolLibrary.getProtocolManager().addPacketListener(PacketListener())

    if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
        Bukkit.getPluginManager().registerEvents(MythicMobListener, ThePit.getInstance());
    }
}
