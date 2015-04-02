# Introduction #

  * JISC project proposal: http://vreri.googlecode.com/files/Bid30%20MILARQ.pdf

# Outline plan from proposal #

Months:
  * 1-2: project setup, implementation plan, outline specification and test plan;
  * 3-4: initial implementation;
  * 5-6: evaluation and refinement;
  * 7-8: CLAROS public deployment, documentation, hand-over to Jena community.

| Month | Activity | Deliverables |
|:------|:---------|:-------------|
| 1 | Initial planning. Project setup, and discussion with Epimorphics about engagement with the open source Jena project. Analyze existing Jena/LARQ code and plan changes. Outline test framework for changes. | Project plan. Publicly hosted project tracking facility and source control (assuming no committer status with Jena project at this stage). Reviewed implementation plan documents. |
| 2 | Agree initial functionality with Epimorphics. Outline test cases for multiple indexes. Code initial test cases. | Initial test cases run-able (not expected to pass). |
| 3 | Implement Jena and/or SPARQLite extensions to satisfy test cases. | Jena/SPARQLite code passes initial test cases. |
| 4 | Continue coding test cases and implementation. Start documenting new APIs and/or configuration. | Initial functionality complete and unit-tested. Initial documentation. |
| 5 | Create CLAROS test deployment. Update documentation in light of experiences. Develop load tests. Run load tests, compare with current CLAROS. | Initial CLAROS deployment with new indexes. Initial performance results. Updated documentation. |
| 6 | Continue deployment, load testing and evaluation. Document findings. Package software for more widespread deployment. Seek code and documentation reviews from Jena community. | Documented evaluation. Refined code, documentation. Deployable software package for updated query service. CLAROS service ready to go public. |
| 7 | Evaluation by CLAROS team. Write up approach and results for general publication. | Public deployment of CLAROS. Documented approach and lessons. |
| 8 | Finalize packaging for deployment. Create patches of contributions to existing open source projects. | Code tested, documented evaluated, lodged with existing open source projects. |

# Sprints #

The project will be run along agile lines, as a series of "sprint"s or iterations, which will aim to achieve the progress outlined above. Each sprint will be planned in greater detail when it starts, adjusting the goals to take account of progress made and user feedback to date.

The target sprint duration is 4 weeks (which is longer than often desired, but reflects (a) the "part-time" nature of the project, and (b) the overhead of having too-frequent meetings between distributed projects).  Sprints 3 and 4 are planned to be shorter as this is anticipated to be where the bulk of development work is progressed.  The final sprint is extended as this is expected to mainly involve "mopping up" activities.

The sprint reviews should be supplemented by weekly progress summaries posted to the project blog, which ''(experimentally)'' may stand in lieu of formal project retrospectives.

| Sprint 1 | 1 Mar  - 9 Apr  | [sprint 1 plan](SprintPlan_1.md) |
|:---------|:----------------|:---------------------------------|
| Sprint 2 | 12 Apr - 30 Apr | [sprint 2 plan](SprintPlan_2.md) |
| Sprint 3 | 26 May - 11 Jun | [sprint 3 plan](SprintPlan_3.md) |
| Sprint 4 | 12 Jun - July | [sprint 4 plan meeting](20100611_Meeting_Project_Progress.md) |
| Sprint 5 | Aug - Sep | [sprint 5 plan meeting](20100730_Meeting_Project_Progress.md) |
| Sprint 6 | Oct - end | [sprint 6 plan meeting](20100930_Meeting_Project_Progress.md) |

(Note: the original sprint schedule has been abandoned: it simply wasn't reflected by the reality of what we could achieve, and trying to do so would impose unreasonable management burden on the project.  In later sprints, the sprint planning process has been relaxed, being replaced by notes from our regular meetings, as these seemed to be serving the needed purposes for this project.)

# Reporting checklist #

See: http://code.google.com/p/vreri/wiki/ProjectDocumentation

| Name | Description | required tags | Done |
|:-----|:------------|:--------------|:-----|
| Project core resources | Key information and links about the project; [online form](http://spreadsheets.google.com/viewform?formkey=dGd3QzNiQnFsZkE3bUZHb2cydkp4TFE6MA); due within 2 weeks of project start | n/a | (pending) |
| Project plan | The Project Plan should clearly state the obligation you have to JISC in how you plan to achieve your project outputs. We understand that these plans are likely to change, nevertheless it should be an accurate estimation for how you intend to achieve success. The Project Plan should be a re-formatted version of your Project Proposal so that it is comprehensible to people outside of the JISC community. | n/a | (pending) |
| Project evaluation | Undertake a basic iterative evaluation and analysis process (e.g.SWOT analysis) of your project. Please also refer to use statistics and analysis of take up. The key is to truly knowing thyself or at least your project. Please give extra attention to any sustainability issues related to this project. | SWOT, projectEvaluation, rapidInnovation, progressPosts, VRERI, JISC, MILARQ |
| User participation | What is the core user case(s) you think of when developing the app; Please find and post #1 testimonial/quote of a real end user, #2 testimonial/quote of someone in the management of your institution. How has this story changed as you have engaged with the end user e.g. "we often talk about Sam the part time working student we met while at...".  Bonus points for actually talking about real end-users you are working with (note: you are not the end user!) | userCase, endUser, rapidInnovation, progressPosts, VRERI, JISC, MILARQ |
| Day-to-day work | What software tools or productivity methods do you use and how do you use them?  How do they make you more productive and why do you see value in using them?  Any and all tools, methodologies or diagrams showing off the way your project is run from an individual or team perspective are welcome. | methodology, implementation, productivity, progressPosts, rapidInnovation, VRERI, JISC, MILARQ |
| Technical standards and reuse | What technologies, frameworks, standards or anything else that makes your life easier (or harder) in your work.  For example: what programming language (or framework, IDE, pattern, etc) do you use and why do you love it, e.g. "why I love Object Relational Mapping in Python and Django" or "why I think SWORD is great but could do this...", please remember to provide links for standards and acronyms, etc. | techStandards, technicalDevelopment, progressPosts, rapidInnovation, VRERI, JISC, MILARQ |
| Value Add | What shifted your thinking in a new direction. What was the most important thing you discovered that brought value to your project, e.g. what was the deciding factor that made you make a specific decision on a what technology, process or human process you were going to use? | valueAdd, disruptiveInnovation, progressPosts, rapidInnovation, VRERI, JISC, MILARQ |
| Small WIN(s) & FAIL(s) | announce small wins for the project, e.g. when you finish a coding sprint or when a user has a 'wow your software is cool' moment. The more of these short "win" posts the better! Also don't forget to post the FAIL(s) as well: telling people where thing went wrong so they don't repeat mistakes is priceless for a thriving community | WIN, progressPosts, rapidInnovation, VRERI, JISC, MILARQ |
|  | _or_ | FAIL, progressPosts, rapidInnovation, VRERI, JISC, MILARQ |
|  | _or_ | progressPosts, rapidInnovation, VRERI, JISC, MILARQ |
| Sprint plans | New and revised plans generated periodically, responding to user requirements and actual prior progress.  Also, meta-issues about project conduct and reporting. | progressPosts, rapidInnovation, VRERI, JISC, MILARQ, Planning |
| Final Progress Post | (See copy of template below) | progressPosts, rapidInnovation, VRERI, JISC, finalProgressPost, output, prototype, product, demonstrator, MILARQ | -- |

Project announcements blog: http://MILARQ-announce.blogspot.com/

An aggregated feed of all VRERI Progress Posts is available at http://pipes.yahoo.com/jisc/rapidinnovationprojectnews.

## Final report template ##

  * Title of Primary Project Output: <--! Please use the following title format for this post: "ProjectTag?: One line description of prototyped output"-->
  * Screenshots or diagram of prototype: <!-- Please provide a series of screenshots or diagram that will quickly explain the point and process of your prototype to the end user. Annotation on screenshots welcome. -->
  * Description of Prototype: <--! Please write this description for the end user so they can easily understand what the prototype is about and how to use it, please be brief and to the point (think Argos Catalogue like description). -->
  * Link to working prototype: <!-- This http link should point directly to a working prototype in which the end user can interact, if a working prototype is not available then please provide a screencast or series of screenshots demonstrating end user functionality, this screencast should not exceed 5 minutes. Please note: working prototypes are preferred even if just 'rough and ready', please do not send powerpoints or other non web-based documents, they will not be accepted. This prototype must be maintained for one year after the date of the official project sign-off. -->
  * Link to end user documentation: <!-- Please provide an http link for a page that explains the use of the prototype to the end user, e.g. an "about" page that explains the project and why it is producing the prototype. For example, what end user problem does it solve, what question does the prototype answer, what itch for the community does it scratch? -->
  * Link to code repository or API: <!-- This http link should be to the primary page listing all code libraries for the prototype. This page must be maintained for one year after the official date of the project sign-off. -->
  * Link to technical documentation: <!-- Please provide an http link for a page listing all technical documentation which explains the code listed in the code repository above. -->
  * Date prototype was launched: <!-- Please provide a date for when the last version of the prototype was made available to end users -->
  * Project Team Names, Emails and Organisations: <!-- For example: "David F. Flanders, d.flanders@jisc.ac.uk - Joint Information Systems Committee; etc." -->
  * Project Website: <!-- HTTP link to any additional wiki, blog or site that has applicable documentation -->
  * PIMS entry: <!-- Please provide a link to your projects entry in PIMS, for example: https://pims.jisc.ac.uk/projects/view/1333 (ignore authentication pop-up) -->
  * Table of Content for Project Posts <!-- A final thematic table of contents should be provided here of all the project posts written along with links to each post. Please make this table of contents generic to any readership that might find your project on the Web. -->

# Effort schedule #

Nominally, the MILARQ project funds 25% of GK's time from March to the end of
October, a total of 43 days of work.  This will run in parallel with the ADMIRAL project which funds 75% of GK's time over the same period.

Thus, this is the effort allocation that is planned for the next few months:

Graham Klyne:
```
M = MILARQ 43 days
A = ADMIRAL
v = planned vacation
1-8 = start of new sprint

2010
March          April       May            June
01.08.15.22.29.05.12.19.26.03.10.17.24.31.07.14.21.28
A1.A-.M-.M-.A-.Av.A2.A-.A-.A3.A-.A-.A4.A-.A-.A5.A-.A-
A-.A-.M-.M-.A-.Av.A-.A-.A-.A-.A-.A-.A-.A-.A-.A-.A-.A-
A-.A-.A-.A-.A-.Av.A-.A-.A-.A-.A-.A-.A-.A-.A-.A-.A-.A-
A-.A-.A-.A-.Av.Av.M-.A-.A-.A-.A-.M-.A-.A-.A-.M-.A-.A-
A-.M-.A-.A-.Mv.Mv.M-.M-.M-.M-.M-.M-.M-.M-.M-.M-.M-.M-
July        August         September   October
05.12.19.26.02.09.16.23.30.06.13.20.27.04.11.18.25
A-.A6.A-.A-.Av.Av.A7.A-.A-.A-.A8.A-.A-.A-.A-.A-.A-
A-.A-.A-.A-.Av.Av.A-.A-.A-.A-.A-.A-.A-.A-.A-.A-.A-
A-.A-.A-.A-.Av.Av.A-.A-.A-.A-.A-.A-.A-.A-.A-.A-.A-
A-.M-.A-.A-.Av.Mv.A-.A-.A-.M-.A-.A-.A-.M-.A-.A-.A-
M-.M-.M-.M-.Mv.Mv.M-.M-.M-.M-.M-.M-.M-.M-.M-.M-.M-
```