#!/usr/bin/perl -w

use File::Find;
use vars qw($ENV $ROOT_PATH @LICENSE);

sub main
{
	$ROOT_PATH = $ENV{'SPARX_HOME'} || ($ENV{HOME} . "/Projects/Sparx/java");
		
	die "SPARX_HOME environment variable not provided." unless $ROOT_PATH;
	die "SPARX_HOME environment variable value is invalid." unless -d $ROOT_PATH;
	
	open(LICENSE, ($ENV{HOME} . "/Projects/Sparx/web-shared/docs/LICENSE.TXT")) || die;
	@LICENSE = <LICENSE>;
	close(LICENSE);
	
	find(\&processJava, $ROOT_PATH);	
}

sub processJava
{
	return unless m/\.java$/;
	
	my $lineNum = 1;
	my @contents = ();
	
	open(SOURCE, $File::Find::name) || warn "Can't open $File::Find::name: $!\n";
	@contents = <SOURCE>;
	close(SOURCE);
	
	#open(DEST, ">$File::Find::name");
	#print DEST @LICENSE;
	#print DEST "\n";
	#print DEST @contents;
	#close(DEST);
}
           
main();
